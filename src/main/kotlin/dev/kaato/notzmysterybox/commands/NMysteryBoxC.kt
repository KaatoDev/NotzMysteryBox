package dev.kaato.notzmysterybox.commands

import dev.kaato.notzmysterybox.managers.CommandManager.addRewardCMD
import dev.kaato.notzmysterybox.managers.CommandManager.createMysteryBoxCMD
import dev.kaato.notzmysterybox.managers.CommandManager.createRewardCMD
import dev.kaato.notzmysterybox.managers.CommandManager.giveMysteryBoxCMD
import dev.kaato.notzmysterybox.managers.CommandManager.giveRewardCMD
import dev.kaato.notzmysterybox.managers.CommandManager.listMysteryBoxesCMD
import dev.kaato.notzmysterybox.managers.CommandManager.listRewardsCMD
import dev.kaato.notzmysterybox.managers.CommandManager.removeRewardCMD
import dev.kaato.notzmysterybox.managers.CommandManager.setDefaultIconCMD
import dev.kaato.notzmysterybox.managers.CommandManager.setIconCMD
import dev.kaato.notzmysterybox.managers.CommandManager.setItemCMD
import dev.kaato.notzmysterybox.managers.CommandManager.setMoneyCMD
import dev.kaato.notzmysterybox.managers.CommandManager.setRewardCMD
import dev.kaato.notzmysterybox.managers.CommandManager.viewMBCMD
import dev.kaato.notzmysterybox.managers.CommandManager.viewMBRewardsCMD
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.containsMysteryBox
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.getMysteryBoxList
import dev.kaato.notzmysterybox.managers.NotzManager.reloadPlugin
import dev.kaato.notzmysterybox.managers.RewardsManager.containsReward
import dev.kaato.notzmysterybox.managers.RewardsManager.getRewardList
import dev.kaato.notzmysterybox.utils.MessageU.send
import dev.kaato.notzmysterybox.utils.MessageU.sendHeader
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.Collections

class NMysteryBoxC : TabExecutor {
    override fun onCommand(p: CommandSender?, cmd: Command?, label: String?, args: Array<out String?>?): Boolean {
        val a = args?.map { it?.lowercase() ?: "" }?.toTypedArray()

        if (p !is Player) {
            if (p == null) return false
            val help = "&eUtilize &f/&enmb &f<&eMysteryBox&f> &egive &f<&eplayer&f&f> &f(&equantidade&f/&epack&f) &7- dá uma MysteryBox ao player."
            if (a.isNullOrEmpty()) {
                send(p, help)
                return true
            }
            val mysteryBox = containsMysteryBox(a[0])
            val mb = if (mysteryBox) a[0] else ""

            if (a.size == 3 || a.size == 4) giveMysteryBoxCMD(p, a[2], mb, if (a.size == 4) a[3] else null)
            else send(p, help)

            return true
        }

        if (!p.hasPermission("notzmysteryboxes.admin")) {
            send(p, "&cSem permissão.")
            return true
        }

        if (a.isNullOrEmpty()) {
            help(p, a)
            return true
        }

//        if (a.isNullOrEmpty()) {
//            help(p, a)
//            return true
//        }

        val mysteryBox = containsMysteryBox(a[0])
        val reward = containsReward(a[0])
        val mb = if (mysteryBox) a[0] else ""
        val rew = if (reward) a[0] else ""
        val help = { help(p, a, mb, rew) }

        when (a.size) {
            1 -> if (a[0] == "reload") reloadPlugin() else help.invoke()
            2 -> if (reward) when (a[1]) {
                "seticon" -> setIconCMD(p, rew)
                "setitem" -> setItemCMD(p, rew)
                else -> help.invoke()

            } else if (mysteryBox) when (a[1]) {
                "view" -> viewMBCMD(p, mb)
                "rewards", "rew" -> viewMBRewardsCMD(p, mb)
                else -> help

            } else when (a[0]) {

                "list" -> when (a[1]) {
                    "mysterybox", "mysteryboxes", "mb" -> listMysteryBoxesCMD(p)
                    "reward", "rewards", "rew" -> listRewardsCMD(p)
                    else -> help.invoke()
                }

                "setdefaulticon" -> setDefaultIconCMD(p, a[1]).let { if (!it) help.invoke() }

                else -> help.invoke()
            }

            3 -> if (mysteryBox) when (a[1]) {
                "remove" -> removeRewardCMD(p, mb, a[2])
                "give" -> giveMysteryBoxCMD(p, a[2], mb)
                else -> help.invoke()

            } else if (reward) when (a[1]) {
                "setmoney" -> setMoneyCMD(p, rew, a[2]).let { if (!it) help.invoke() }
                "give" -> giveRewardCMD(p, a[2], rew).let { if (!it) help.invoke() }
                else -> help.invoke()
            }
            else help.invoke()

            4 -> if (mysteryBox) when (a[1]) {
                "add" -> addRewardCMD(p, mb, a[2], a[3]).let { if (!it) help.invoke() }
                "set" -> setRewardCMD(p, mb, a[2], a[3]).let { if (!it) help.invoke() }
                "give" -> giveMysteryBoxCMD(p, a[2], mb, a[3]).let { if (!it) help.invoke() }
                else -> help.invoke()

            } else if (a[0] == "create") when (a[1]) {
                "reward" -> createRewardCMD(p, a[2], a[3]).let { if (!it) help.invoke() }
                "mysterybox" -> createMysteryBoxCMD(p, a[2], a[3])
                else -> help.invoke()

            } else help.invoke()

            5 -> if (a[0] == "create" && a[1] == "reward") {
                createRewardCMD(p, a[2], a[3], a[4])
            } else help.invoke()

            else -> help.invoke()
        }
        return true
    }

    override fun onTabComplete(p: CommandSender?, cmd: Command?, label: String?, args: Array<out String?>?): List<String?>? {
        if (p !is Player) return null

        val a = args?.map { it?.lowercase() ?: "" }?.toTypedArray()

        if (a == null) return listOf("create", "list", "reload", "setDefaultIcon", "<MysteryBox>").plus(getMysteryBoxList()).plus("<Rewards>").plus(getRewardList())

        val mysteryBox = containsMysteryBox(a[0])
        val reward = containsReward(a[0])
        val mb = if (mysteryBox) a[0] else ""
        val rew = if (reward) a[0] else ""

        return when (a.size) {
            0 -> listOf("create", "list", "reload", "setDefaultIcon", "<MysteryBox>").plus(getMysteryBoxList()).plus("<Rewards>").plus(getRewardList())
            1 -> if (mysteryBox) listOf("add", "give", "remove", "set")
            else if (reward) listOf("give", "setIcon", "setItem", "setMoney")
            else if (a[0] == "create") listOf("mysteryBox", "reward") else Collections.emptyList()

            else -> Collections.emptyList()
//            2 -> if (mysteryBox) listOf("add", "give", "remove", "set")
        }
    }

    fun help(p: Player, a: Array<String>?, mb: String? = null, rew: String? = null) {
        val utilize = "&eUtilize &f/&enmb &e"
        val help = """
            &7+ &f<&emysteybox&f> &7- visualiza os comandos de MysteyBox.
            &7+ &f<&ereward&f> &7- visualiza o comando das rewards.
            &7+ &ecreate mysteryBox &f<&ename&f> &f<&etrue&f/&efalse&f> &7- cria uma MysteryBox e seta com encantamento ou sem (precisará ser editada na config).
            &7+ &ecreate reward &f<&ename&f> &f<&eCOMMAND&f/&eITEM&f/&eMONEY&f/&eMESSAGE&f> &f(&emoney&f) &7- cria uma reward (apenas o money pode ser setado diretamente por aqui).
            &7+ &elist mysteryBox &7- lista todas as MysteryBoxes criadas.
            &7+ &elist reward &7- lista todas a Rewards criadas.
            &7+ &ereload &7- recarrega os arquivos do plugin.
            &7+ &esetDefaultIcon &f<&eCOMMAND&f/&eITEM&f/&eMONEY&f/&eMESSAGE&f> &7- seta o ícone padrão para o tipo da reward.
        """.trimIndent()

        if (!mb.isNullOrEmpty()) helpMB(p, a!!, mb)
        else if (!rew.isNullOrEmpty()) helpRew(p, a!!, rew)
        else if (a.isNullOrEmpty()) sendHeader(p, "$utilize+\n$help")
        else sendHeader(
            p, utilize + when (a[0]) {
                "create" -> """create &7+
                    &7+ &emysteryBox &f<&ename&f> &f<&etrue&f/&efalse&f> &7- cria uma MysteryBox e seta com encantamento ou sem (precisará ser editada na config).
                    &7+ &ereward &f<&ename&f> &f<&eCOMMAND&f/&eITEM&f/&eMONEY&f/&eMESSAGE&f> &f(&emoney&f) &7- cria uma reward (apenas o money pode ser setado diretamente por aqui).
                """.trimIndent()

                "list" -> """list &7+
                    &7+ &emysteryBox &7- lista todas as MysteryBoxes criadas.
                    &7+ &ereward &7- lista todas a Rewards criadas.
                """.trimIndent()

                "reload" -> "reload &7- recarrega os arquivos do plugin."
                "setDefaultIcon" -> "setDefaultIcon &f<&eCOMMAND&f/&eITEM&f/&eMONEY&f/&eMESSAGE&f> &7- seta o ícone padrão para o tipo da reward."
                "view" -> "view - visualiza o conteúdo da caixa."
                else -> "+\n$help"
            }
        )
    }

    fun helpMB(p: Player, a: Array<String>, mb: String) {
        val utilize = "&eUtilize &f/&enmb &b$mb &e"

        val help = """
            &7+ &eadd &f<&ereward&f> &f<&echance&f> &7- adiciona uma reward à MysteryBox.
            &7+ &egive &f<&eplayer&f&f> &f(&equantity&f/&epack&f) &7- dá um MysteryBox ao player.
            &7+ &eremove &f<&ereward&f> &7- remove uma reward da MysteryBox.
            &7+ &eset &f<&ereward&f> &f<&echance&f> &7- altera a chance de uma reward da MysteryBox.
        """.trimIndent()

        if (a.size == 1) sendHeader(p, "$utilize+\n$help")
        else sendHeader(
            p, utilize + when (a[1]) {
                "add" -> "add &f<&ereward&f> &f<&echance&f> &7- adiciona uma reward à MysteryBox."
                "give" -> "give &f<&eplayer&f&f> &f(&equantity&f/&epack&f) &7- dá um MysteryBox ao player."
                "remove" -> "remove &f<&ereward&f> &7- remove uma reward da MysteryBox."
                "set" -> "set &f<&ereward&f> &f<&echance&f> &7- altera a chance de uma reward da MysteryBox."
                else -> "+\n$help"
            }
        )
    }

    fun helpRew(p: Player, a: Array<String>, rew: String) {
        val utilize = "&eUtilize &f/&enmb &b$rew &e"

        val help = """
            &7+ &egive &f(&eplayer&f&f) &7- dá uma Reward ao player.
            &7+ &esetIcon &7- seta o ícone da Reward.
            &7+ &esetItem &7- seta o item da premiação da Reward.
            &7+ &esetMoney &f<&emoney&f> &7- seta o a quantia de dinheiro da premiação da Reward.
        """.trimIndent()

        if (a.size == 1) sendHeader(p, "$utilize+\n$help")
        else sendHeader(
            p, utilize + when (a[1]) {
                "give" -> "give &f(&eplayer&f&f) &7- dá uma Reward ao player."
                "setIcon" -> "setIcon &7- seta o ícone da Reward."
                "setItem" -> "setItem &7- seta o item da premiação da Reward."
                "setMoney" -> "setMoney &f<&emoney&f> &7- seta a quantia de dinheiro da premiação da Reward."
                else -> "+\n$help"
            }
        )
    }
}