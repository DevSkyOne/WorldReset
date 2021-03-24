package cloud.melion.survival.worldreset

import cloud.melion.survival.worldreset.utils.console
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.WorldCreator

import java.io.IOException

import java.io.FileInputStream

import java.util.Properties
class Worldreset : JavaPlugin() {

    val backupWorlds = File("backupWorlds")
    val overworlds = File(backupWorlds, "overworlds")
    val nethers = File(backupWorlds, "nethers")
    val ends = File(backupWorlds, "ends")

    override fun onLoad() {
        "§9WorldReset §f• §eWelten werden überprüft.".console()
        if (!backupWorlds.isDirectory) {
            backupWorlds.mkdirs()
            overworlds.mkdir()
            nethers.mkdir()
            ends.mkdir()
            "§9WorldReset §f• §eBackupverzeichnis erstellt. Bitte ziehe deine Welten in §f`/backupWorlds/overworlds`".console()
            "§9WorldReset §f• §cAchte darauf alle Playerdaten zu löschen.".console()
            return
        } else {
            // Migration
            "§9WorldReset §f• §eMigration wird ausgeführt.".console()
            if (!overworlds.isDirectory) {
                val worlds = backupWorlds.listFiles()

                overworlds.mkdir()
                nethers.mkdir()
                ends.mkdir()

                for (file in worlds) {
                    if (file.absolutePath == backupWorlds.absolutePath) continue
                    val worldFolder = File(overworlds, file.name)
                    FileUtils.moveDirectory(file, worldFolder)
                }
            }
            "§9WorldReset §f• §eMigration abgeschlossen.".console()
        }

        "§9WorldReset §f• §eZufällige Welt wird gesucht.".console()
        val overworld: File? = getRandomWorld(overworlds)
        val nether: File? = getRandomWorld(nethers)
        val end: File? = getRandomWorld(ends)

        if(overworld == null && nether == null && end == null) {
            "§9WorldReset §f• §cReset wird abgebochen.".console()
            return
        }

        overworld?.let { world ->
            "§9WorldReset §f• §eOverworld ausgewählt: §9${world.name}".console()
            resetWorld(File(levelName), world)
        }

        nether?.let { world ->
            "§9WorldReset §f• §eNether ausgewählt: §9${world.name}".console()
            resetWorld(File("${levelName}_nether"), world)
        }

        end?.let { world ->
            "§9WorldReset §f• §eEnd ausgewählt: §9${world.name}".console()
            resetWorld(File("${levelName}_the_end"), world)
        }

        "§9WorldReset §f• §aReset erfolgreich.".console()
    }

    private val levelName: String
    get() {
        val prop = Properties()
        try {
            prop.load(FileInputStream(File("server.properties")))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return prop.getProperty("level-name") ?: "world"
    }

    private fun resetWorld(worldFolder: File, newWorld: File) {
        if (worldFolder.isDirectory) {
            "§9WorldReset §f• §e${worldFolder.name} wird gelöscht.".console()
            FileUtils.deleteDirectory(worldFolder)
        }

        "§9WorldReset §f• §e${newWorld.name} wird als ${worldFolder.name} eingefügt.".console()
        worldFolder.mkdirs()
        FileUtils.copyDirectory(newWorld, worldFolder)
    }

    private fun getRandomWorld(directory: File): File? {
        val worlds = directory.listFiles()?.filter { file -> file.isDirectory && file.name.endsWith("(UNLOADED)").not() }
        if (worlds != null && worlds.isNotEmpty()) {
            return worlds.random()
        }
        "§9WorldReset §f• §cKeine BackupWorld für ${directory.name} gefunden.".console()
        return null
    }
}