package dev.kaato.notzmysterybox.utils

import dev.kaato.notzmysterybox.Main.Companion.cf
import dev.kaato.notzmysterybox.Main.Companion.prefix
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object MessageU {
    val messages = hashMapOf<String, String>()

    init {
        prefix = c(cf.config.getString("prefix"))
    }

    fun c(message: String): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }

    fun send(p: CommandSender, msg: String) {
        p.sendMessage(c("$prefix $msg"))
    }

    fun sendText(p: Player, txt: String) {
        val msg = messages[txt] ?: return
        send(p, msg)
    }

    fun sendHeader(player: Player, message: String) {
        val msg = """
            &r
            &f-=-=-=-&5= $prefix &5=&f-=-=-=-
            $message
            &r
        """.replace("            ", "")
//        """.replace("            ", "  ").replace("+ ", "  + ")

        player.sendMessage(c(msg))
    }
}