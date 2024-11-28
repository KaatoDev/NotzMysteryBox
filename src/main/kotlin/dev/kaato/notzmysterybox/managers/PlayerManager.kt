package dev.kaato.notzmysterybox.managers

import dev.kaato.notzmysterybox.Main.Companion.plugin
import dev.kaato.notzmysterybox.entities.enums.RewardType
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.getMysteryBoxReward
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.isMysteryBox
import dev.kaato.notzmysterybox.managers.RewardsManager.openReward
import dev.kaato.notzmysterybox.utils.GuiU.animateOpenMB
import dev.kaato.notzmysterybox.utils.MessageU.send
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

object PlayerManager {
    fun openMysterybox(p: Player, item: ItemStack): Boolean? {
        val mysteryBox = isMysteryBox(item) ?: return false
        if (mysteryBox.rewards.isEmpty) return null
        val rew = getMysteryBoxReward(mysteryBox)

        if (rew == null) {
            send(p, "&cEsta caixa ainda não possui recompensas associadas!")
            return false
        }

        val reward = openReward(p, rew)

        if (reward != null) {
            animateOpenMB(p, mysteryBox, reward.item?:reward.icon)

            object : BukkitRunnable() {
                override fun run() {
                    send(p, "&eVocê abriu uma ${mysteryBox.display}&e!")
                    if (reward.rewardType == RewardType.MESSAGE)
                        send(p, reward.message)
                }
            }.runTaskLater(plugin, 5 * 20)
            p.inventory.removeItem(mysteryBox.getItem())
            return true

        } else send(p, "&cNão foi possível entregar a recompensa $rew.")

        return false
    }
}