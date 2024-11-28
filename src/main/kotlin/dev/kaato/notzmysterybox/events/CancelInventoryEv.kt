package dev.kaato.notzmysterybox.events

import dev.kaato.notzmysterybox.managers.MysteryBoxManager.getMysteryBoxTypes
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.isMysteryBox
import dev.kaato.notzmysterybox.utils.GuiU.clearPlayerTask
import dev.kaato.notzmysterybox.utils.GuiU.guiTitle
import dev.kaato.notzmysterybox.utils.GuiU.guiTitleView
import dev.kaato.notzmysterybox.utils.GuiU.removePlayerTask2
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerQuitEvent

class CancelInventoryEv : Listener {
    private val inventories = arrayOf(InventoryType.PLAYER, InventoryType.CHEST, InventoryType.CHEST, InventoryType.CRAFTING)

    @EventHandler(priority = EventPriority.HIGHEST)
    fun cancelInventory(e: InventoryClickEvent) {
        val item = e.currentItem

        if (e.inventory.title == guiTitle || e.inventory.title == guiTitleView) {
            e.isCancelled = true
            e.result = Event.Result.DENY
            return
        }

        if (arrayOf(e.inventory, e.clickedInventory, e.currentItem).contains(null) || inventories.contains(e.inventory.type)) return


        if (e.click == ClickType.NUMBER_KEY || getMysteryBoxTypes().contains(item.type) && isMysteryBox(item) != null) {
            e.isCancelled = true
            e.result = Event.Result.DENY
        }
    }

    @EventHandler
    fun closeInvMB(e: InventoryCloseEvent) {
        if (e.inventory.title == guiTitle) {
            removePlayerTask2(e.player as Player)
        }
    }

    @EventHandler
    fun leavePlayer(e: PlayerQuitEvent) {
        clearPlayerTask(e.player)
    }
}