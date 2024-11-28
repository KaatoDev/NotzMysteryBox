package dev.kaato.notzmysterybox.managers

import dev.kaato.notzmysterybox.entities.enums.RewardType
import dev.kaato.notzmysterybox.entities.enums.RewardType.*
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.addReward
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.createMysteryBox
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.getMysteryBox
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.getMysteryBoxList
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.isMysteryBox
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.removeReward
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.setReward
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.viewRewardsMB
import dev.kaato.notzmysterybox.managers.NotzManager.setDefaultIcon
import dev.kaato.notzmysterybox.managers.RewardsManager.containsReward
import dev.kaato.notzmysterybox.managers.RewardsManager.createReward
import dev.kaato.notzmysterybox.managers.RewardsManager.forceOpenReward
import dev.kaato.notzmysterybox.managers.RewardsManager.getRewardList
import dev.kaato.notzmysterybox.managers.RewardsManager.setIcon
import dev.kaato.notzmysterybox.managers.RewardsManager.setItem
import dev.kaato.notzmysterybox.managers.RewardsManager.setMessage
import dev.kaato.notzmysterybox.managers.RewardsManager.setMoney
import dev.kaato.notzmysterybox.utils.GuiU.viewContents
import dev.kaato.notzmysterybox.utils.MessageU.send
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object CommandManager {
    fun setDefaultIconCMD(p: Player, type: String): Boolean {
        val icon = p.itemInHand
        if (checkItem(p, icon)) {
            if (setDefaultIcon(type, icon)) send(p, "&eO ícone padrão do tipo &f${type} &efoi alterado.")
            else return false
        }
        return true
    }

    fun setIconCMD(p: Player, rew: String) {
        val icon = p.itemInHand
        if (checkItem(p, icon)) {
            if (isMysteryBox(icon) != null) send(p, "&cVocê não pode setar uma caixa como ícone de uma recompensa, crie uma réplica dela.")
            if (setIcon(rew, icon)) send(p, "&eO ícone da reward &f$rew &efoi alterado com &asucesso&e.")
            else rewardNotFound(p)
        }
    }

    fun setItemCMD(p: Player, rew: String) {
        val item = p.itemInHand
        if (checkItem(p, item)) {
            if (isMysteryBox(item) != null) send(p, "&cUtilize comandos para dar uma caixa como recompensa.")
            else if (setItem(rew, item)) send(p, "&eO item da reward &f$rew &efoi alterado com &asucesso&e.")
            else rewardNotFound(p)
        }
    }

    fun setMessageCMD(p: Player, rew: String, message: String) {
        if (setMessage(rew, message)) send(p, "&eA menssagem da reward &f$rew &efoi alterada com &asucesso&e.")
        else rewardNotFound(p)
    }

    fun setMoneyCMD(p: Player, rew: String, moneyStr: String): Boolean {
        var money = 0.0
        try {
            money = moneyStr.toDouble()
        } catch (e: NumberFormatException) {
            send(p, "&cUtilize uma quantia válida!")
            return false
        }

        if (setMoney(rew, money)) send(p, "&eA quantia da reward &f$rew &efoi alterada para &2$&a$money&e.")
        else rewardNotFound(p)

        return true
    }

    fun removeRewardCMD(p: Player, mb: String, rew: String) {
        when (removeReward(mb, rew)) {
            true -> send(p, "&eA reward &f$rew &efoi removida da caixa &f$mb&e.")
            false -> send(p, "&cReward não encontrada na caixa.")
            null -> mysteryBoxNotFound(p)
        }
    }

    fun addRewardCMD(p: Player, mb: String, rew: String, chanceStr: String): Boolean {
        if (!containsReward(rew)) {
            send(p, "&cEsta Reward não existe")
            return true
        }

        var chance = 0.0
        try {
            chance = chanceStr.toDouble()
        } catch (e: NumberFormatException) {
            send(p, "&cUtilize uma quantia válida!")
            return false
        }

        when (addReward(mb, rew, chance)) {
            true -> send(p, "&eA reward &f$rew &efoi adicionada à caixa &f$mb&e.")
            false -> send(p, "&cEsta reward já está setada na caixa! Utilize a opção 'set' no comando para alterá-la.")
            null -> mysteryBoxNotFound(p)
        }
        return true
    }

    fun setRewardCMD(p: Player, mb: String, rew: String, chanceStr: String): Boolean {
        var chance = 0.0
        try {
            chance = chanceStr.toDouble()
        } catch (e: NumberFormatException) {
            send(p, "&cUtilize uma quantia válida!")
            return false
        }

        when (setReward(mb, rew, chance)) {
            3 -> send(p, "&eChance da reward &f$rew &esetada para &a$chance% &ena caixa &f$mb&e.")
            2 -> send(p, "&cEsta reward não está adicionada na caixa! Utilize a opção 'add' no comando para setá-la.")
            1 -> send(p, "&cA chance setada e a sugerida são iguais!")
            else -> mysteryBoxNotFound(p)
        }
        return true

    }

    fun createRewardCMD(p: Player, name: String, typeStr: String, moneyStr: String? = null): Boolean {
        val type = RewardType.entries.find { it.name == typeStr.uppercase() }
        val item = p.itemInHand
        var money = 0.0

        if (type == null) {
            send(p, "&cInsira um tipo válido de reward!")
            return false
        }

        val wasCreated = if (type == ITEM) {
            if (checkItem(p, item)) {
                createReward(name, type, item)
            } else return true
        } else if (type == MONEY && moneyStr != null) {
            try {
                println("aaaaaaa")
                money = moneyStr.toDouble()
            } catch (e: NumberFormatException) {
                send(p, "&cUtilize uma quantia válida!")
                return false
            }
            createReward(name, type, money)
        } else createReward(name, type)

        when (wasCreated) {
            true -> send(p, "&eA reward &f$name &efoi criada com &asucesso&e!")
            false -> send(p, "&cJá existe uma reward com este nome!")
            null -> send(p, "&cNão foi possível criar esta reward pois existe uma caixa com o nome sugerido.")
        }

        return true
    }

    fun createMysteryBoxCMD(p: Player, name: String, isEnchantedStr: String) {
        val isEnchanted = isEnchantedStr == "true"

        when (createMysteryBox(name, isEnchanted)) {
            true -> send(p, "&eA caixa &f$name &ffoi criada com &asucesso&e!")
            false -> send(p, "&cJá existe uma caixa com este nome!")
            null -> send(p, "&cNão foi possível criar esta caixa pois existe uma reward com o nome sugerido.")
        }
    }

    fun giveMysteryBoxCMD(admin: CommandSender, player: String, mb: String, quantityStr: String? = null, sendMessage: Boolean = true): Boolean {
        val mysteryBox = getMysteryBox(mb)
        var quantity = 1
        val p = Bukkit.getPlayer(player)

        if (p == null) {
            playerNotFound(admin)
            return false
        }


        if (quantityStr != null) {
            if (quantityStr == "pack") quantity = 64
            else try {
                quantity = quantityStr.toInt()
                if (quantity > 64) {
                    send(admin, "&cA quantidade máxima possível que pode ser dado de caixa é de &a64&e.")
                    return true
                }
            } catch (e: NumberFormatException) {
                send(p, "&cUtilize uma quantia válida!")
                return false
            }
        }

        if (mysteryBox != null) {
            val box = mysteryBox.getItem().clone()
            box.amount = quantity
            p.inventory.addItem(box)
            if (sendMessage) send(p, "&eVocê recebeu &a$quantity&fx ${mysteryBox.display}&e.")
            send(admin, "&eVocê enviou &a$quantity&fx ${mysteryBox.name} (${mysteryBox.display}&f) &epara o player &f${p.name}&e.")

        } else mysteryBoxNotFound(p)

        return true
    }

    fun giveRewardCMD(admin: Player, player: String, rew: String): Boolean {
        val p = Bukkit.getPlayer(player)

        if (p == null) {
            playerNotFound(admin)
            return false
        }

        val reward = forceOpenReward(p, rew)

        if (reward != null) {
            send(p, "&eVocê recebeu a &freward $rew&e.")
            send(admin, "&eVocê enviou a reward ${reward.name} &epara o player &f${p.name}&e.")
        } else rewardNotFound(p)

        return true
    }

    private fun checkItem(p: Player, item: ItemStack?): Boolean {
        return if (item == null) {
            send(p, "&cVocê precisa estar segurando um item válido na mão!")
            false
        } else true
    }

    private fun rewardNotFound(p: Player) {
        send(p, "&cEsta reward não foi encontrada.")
    }

    private fun mysteryBoxNotFound(p: Player) {
        send(p, "&cEsta caixa não foi encontrada.")
    }

    private fun playerNotFound(p: CommandSender) {
        send(p, "&cEste player não existe ou está offline.")
    }

    fun listMysteryBoxesCMD(p: Player) {
        send(p, getMysteryBoxList().sorted().joinToString(prefix = "&e", separator = "&f, &e", postfix = "&f."))
    }

    fun listRewardsCMD(p: Player) {
        send(p, getRewardList().sorted().joinToString(prefix = "&e", separator = "&f, &e", postfix = "&f."))
    }

    fun viewMBCMD(p: Player, mb: String) {
        when (viewContents(p, mb)) {
            true -> send(p, "&eVisualizando caixa &f$mb&e.")
            false -> send(p, "&cCaixa &f$mb &cnão encontrada.")
            null -> send(p, "&cEsta caixa não contém rewards.")
        }
    }

    fun viewMBRewardsCMD(p: Player, mb: String) {
        val rewards = viewRewardsMB(mb)

        if (rewards == null) send(p, "&cCaixa não encontrada.")
        else if (rewards.isEmpty) send(p, "&cEsta caixa não possui rewards setadas.")
        else send(p, rewards.keys.joinToString( separator = "&e | ", postfix = "&e.") {
            "&f$it: &a${rewards[it]}%"
        })
    }
}