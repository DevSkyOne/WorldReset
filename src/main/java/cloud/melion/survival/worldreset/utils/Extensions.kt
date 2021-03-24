package cloud.melion.survival.worldreset.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor

fun String.console() = Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', this))