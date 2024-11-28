package dev.kaato.notzmysterybox.events

import dev.kaato.notzmysterybox.managers.MysteryBoxManager.getMysteryBoxTypes
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.isMysteryBox
import dev.kaato.notzmysterybox.managers.PlayerManager.openMysterybox
import dev.kaato.notzmysterybox.utils.MessageU.send
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class OpenMysteryBoxEv : Listener {
    @EventHandler
    fun openMB(e: PlayerInteractEvent) {
        if (e.item == null || e.item.type == Material.AIR || !(getMysteryBoxTypes().contains(e.item.type) && isMysteryBox(e.item) != null)) return

        val p = e.player
        val item = e.item.clone()
        val itemHand = try {
            p.inventory.itemInMainHand.clone()
        } catch (e: NoSuchMethodError) {
            p.itemInHand.clone()
        }

        if (!item.isSimilar(itemHand)) return

        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
            e.isCancelled = true
            openMysterybox(p, item).let {
                if (it == null)
                    send(p, "&cEsta caixa não possui rewards setadas ainda.")
                else if (!it)
                    send(p, "&cNão foi possível abrir esta caixa")
            }

        }
    }
}