package dev.kaato.notzmysterybox.events

import dev.kaato.notzmysterybox.Main.Companion.started
import dev.kaato.notzmysterybox.utils.MessageU.send
import org.bukkit.Bukkit
import org.bukkit.Material.*
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent

class PreventMysteryBox : Listener {
    private val boxes = arrayOf(CHEST, ENDER_CHEST, TRAPPED_CHEST, ENDER_PORTAL, BEACON)
    private val msg2 = "&5NotzMysteryBox &afoi inicializado e as prevenções de colocação e manipulação de baús foram removidas!"

    @EventHandler
    fun openMb(e: BlockPlaceEvent) {
        val block = e.block

        if (!started) {
            if (boxes.contains(block.type)) {
                send(Bukkit.getConsoleSender(), "&5NotzMysteryBox &cnão inicializado ainda! Prevenção de colocação de baús ativada.")
                e.isCancelled = true
            }

        } else unregisterThisEvent()

    }

    @EventHandler
    fun cancelInventory(e: InventoryClickEvent) {
        val item = e.currentItem
        if (e.clickedInventory == null || item == null || item.type == AIR || !boxes.contains(item.type)) return

        if (!started) {
            if (boxes.contains(item.type)) {
                send(Bukkit.getConsoleSender(), "&5NotzMysteryBox &cnão inicializado ainda! Prevenção de manipulação de baús ativada.")
                e.isCancelled = true
            }

        } else unregisterThisEvent()
    }

    private fun unregisterThisEvent() {
        send(Bukkit.getConsoleSender(), msg2)
        HandlerList.unregisterAll(this)
    }
}