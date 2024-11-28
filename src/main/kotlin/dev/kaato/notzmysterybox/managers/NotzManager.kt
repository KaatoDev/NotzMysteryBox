package dev.kaato.notzmysterybox.managers

import dev.kaato.notzmysterybox.Main.Companion.cf
import dev.kaato.notzmysterybox.entities.enums.RewardType
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.loadMysteryBoxes
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.reloadMysteryBoxes
import dev.kaato.notzmysterybox.managers.RewardsManager.loadRewards
import dev.kaato.notzmysterybox.managers.RewardsManager.reloadRewards
import dev.kaato.notzmysterybox.utils.GuiU.animateGlass
import dev.kaato.notzmysterybox.utils.GuiU.glassColor
import dev.kaato.notzmysterybox.utils.GuiU.guiTitle
import dev.kaato.notzmysterybox.utils.GuiU.guiTitleView
import dev.kaato.notzmysterybox.utils.MessageU.c
import org.bukkit.inventory.ItemStack

object NotzManager {
    val defaultIcons = hashMapOf<RewardType, ItemStack>()

    fun getDefaultIcon(rewType: RewardType): ItemStack? {
        return defaultIcons[rewType]
    }

    fun setDefaultIcon(type: String, icon: ItemStack): Boolean {
        if (RewardType.entries.find { it.name == type.uppercase() } == null) return false

        val rewType = RewardType.valueOf(type.uppercase())
        defaultIcons[rewType] = icon

        cf.config.set("default-icons.${rewType.name}", icon)
        cf.saveConfig()

        return true
    }

    fun reloadPlugin() {
        reloadRewards()
        reloadMysteryBoxes()
        reloadDefaultIcons()
    }

    fun loadPlugin() {
        loadMysteryBoxes()
        loadRewards()
        load()
    }

    fun load() {
        RewardType.entries.forEach {
            defaultIcons[it] = cf.config.getItemStack("default-icons.${it.name}") ?: return@forEach
        }
        guiTitle = c(cf.config.getString("gui-title") ?: "&6[&fNotzMysteryBox&6]")
        guiTitleView = c("$guiTitle &eView &f")
        animateGlass = cf.config.getBoolean("animate-glass")
        glassColor = cf.config.getInt("glass-color").toShort()
    }

    fun reloadDefaultIcons() {
        defaultIcons.clear()
        load()
    }
}