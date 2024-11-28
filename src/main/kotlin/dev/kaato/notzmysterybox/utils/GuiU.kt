package dev.kaato.notzmysterybox.utils

import dev.kaato.notzmysterybox.Main.Companion.plugin
import dev.kaato.notzmysterybox.entities.MysteryBox
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.getMysteryBox
import dev.kaato.notzmysterybox.managers.RewardsManager.getReward
import dev.kaato.notzmysterybox.utils.MessageU.c
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import kotlin.random.Random

object GuiU {
    private val playerTasks = hashMapOf<OfflinePlayer, MutableList<BukkitTask>>()
    var guiTitle = c("&6[&fNotzMysteryBox&6]")
    var guiTitleView = c("&6[&fNotzMysteryBox&6] &eView")
    var animateGlass = true
    var glassColor: Short = 0
    var contentsViewFull = arrayOf<ItemStack>()
    var contentsViewPartial = arrayOf<ItemStack>()

    init {
        val gui = Bukkit.createInventory(null, 54, guiTitleView)
        contentsViewPartial = gui.contents.clone()
        contentsViewFull = gui.contents.clone()
        val glasses = IntArray(9) { it }.plus(IntArray(9) { it + 45 })
        val glass = ItemStack(Material.STAINED_GLASS_PANE).clone()
        glasses.forEach { contentsViewPartial[it] = glass }
        contentsViewFull = contentsViewPartial
        intArrayOf(9, 17, 18, 26, 27, 35, 36, 44).forEach { contentsViewFull[it] = glass }
    }

    fun viewContents(p: Player, mb: String): Boolean? {
        val mysteryBox = getMysteryBox(mb) ?: return false
        if (mysteryBox.rewards.isEmpty) return null

        val gui = Bukkit.createInventory(null, 54, guiTitleView)
        val rewards = mysteryBox.rewards.keys.map { getReward(it)?.let { it.item ?: it.icon } }.filterNotNull()
        val full = rewards.size.let { if (it > 36) null else it < 29 }
        val contents = full?.let { if (it) contentsViewFull else contentsViewPartial } ?: gui.contents

        gui.contents = contents
        if (rewards.size < 55) {
            contents[4] = mysteryBox.getItem().clone()
            gui.contents = contents
            rewards.forEach { gui.addItem(it.clone()) }
        } else rewards.forEachIndexed { i, it -> if (i > 53) return@forEachIndexed else gui.addItem(it.clone()) }

        p.openInventory(gui)
        return true
    }

    fun clearPlayerTask(p: OfflinePlayer) {
        if (playerTasks.containsKey(p) && playerTasks[p]!!.isNotEmpty()) {
            playerTasks[p]?.forEach(BukkitTask::cancel)
            playerTasks.remove(p)
        }
    }

    fun removePlayerTask2(p: Player) {
        if (playerTasks.containsKey(p) && playerTasks[p]!!.isNotEmpty()) {
            playerTasks[p]!!.last().cancel()
        }
    }

    fun animateOpenMB(p: Player, mb: MysteryBox, item: ItemStack) {
        val gui = Bukkit.createInventory(null, 27, guiTitle)
        val rewards = mb.rewards.keys.map { getReward(it)?.let { it.item ?: it.icon } }.filterNotNull()
        var items = Array(rewards.size.coerceAtLeast(9)) { rewards[it % rewards.size] }
        var finalItems = createFinalItems(items, item)
        val contents = gui.contents.map { ItemStack(Material.STAINED_GLASS_PANE, 1, glassColor) }.toTypedArray()
        val glasses = Array(15) { ItemStack(Material.STAINED_GLASS_PANE, 1, (if (it < 8) it else it + 1).toShort()) }
        val offPlayer: OfflinePlayer = p

        animate(contents, items, glasses)
        gui.contents = contents
        p.openInventory(gui)
        var count = 0

        val animate = object : BukkitRunnable() {
            override fun run() {
                items = rotateIt(items)
                animate(contents, items, glasses)
                val inv = p.openInventory.topInventory
                if (inv.title == guiTitle) inv.contents = contents
                if (count % 2 == 0) p.playSound(p.location, Sound.entries.find { it.name.contains("ORB_PICKUP") }, 0.05f, Random.nextDouble(1.15).toFloat() + 0.5f)
                count++
            }
        }.runTaskTimer(plugin, 0, 1)

        if (playerTasks[p] == null) playerTasks[p] = mutableListOf()

        playerTasks[p]!!.add(animate)

        object : BukkitRunnable() {
            override fun run() {
                try {
                    val inv = p.openInventory.topInventory
                    playerTasks[p]?.removeFirst()

                    if (playerTasks[p]!!.isEmpty() && inv.title == guiTitle) {
                        animate.cancel()

                        for (i in 9..17) contents[i] = finalItems[i - 9]
                        inv.contents = contents
                    }
                } catch (_: NullPointerException) {
                    clearPlayerTask(offPlayer)
                }
            }
        }.runTaskLater(plugin, 5 * 20)
    }

    private fun animate(contents: Array<ItemStack>, items: Array<ItemStack>, glasses: Array<ItemStack>) {
        for (i in 0..26) {
            if (i in 9..17) {
                contents[i] = items[i - 9]
            } else if (animateGlass) {
                if (i < 9) {
                    contents[i] = glasses[i]
                } else {
                    contents[i] = glasses[i - 12]
                }
            }
        }
        if (animateGlass) glasses.shuffle()
    }

    private fun rotateIt(array: Array<ItemStack>): Array<ItemStack> {
        return array.slice(1..(array.size - 1)).plus(array.first()).toTypedArray()
    }

    private fun createFinalItems(array: Array<ItemStack>, icon: ItemStack): Array<ItemStack> {
        var items: Array<ItemStack> = arrayOf()

        array.let {
            val i = it.indexOf(icon)
            items = if (it.size < 10) it
            else if (i > it.size - 5) it.slice(it.size - 9..it.size - 1).toTypedArray()
            else if (i < 5) it.slice(0..8).toTypedArray()
            else it.slice(i - 4..i + 4).toTypedArray()
        }

        val x = 4 - items.indexOf(icon)
        if (x == 0) return items
        val z = if (x < 1) -x else 8 - x

        val arr = if (x < 1) items.slice(z..8).plus(items.slice(0..z - 1)) else items.slice(z + 1..8).plus(items.slice(0..z))

        return arr.toTypedArray()
    }
}