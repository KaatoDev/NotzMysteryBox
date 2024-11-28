package dev.kaato.notzmysterybox.managers

import dev.kaato.notzmysterybox.Main.Companion.cf
import dev.kaato.notzmysterybox.Main.Companion.econ
import dev.kaato.notzmysterybox.Main.Companion.plugin
import dev.kaato.notzmysterybox.entities.Reward
import dev.kaato.notzmysterybox.entities.enums.RewardType
import dev.kaato.notzmysterybox.entities.enums.RewardType.*
import dev.kaato.notzmysterybox.managers.MysteryBoxManager.containsMysteryBox
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

object RewardsManager {
    private val rewards = hashMapOf<String, Reward>()

    fun containsReward(name: String): Boolean {
        return rewards.containsKey(name)
    }

    fun getReward(name: String): Reward? {
        return rewards[name]
    }

    fun getRewardList(): MutableSet<String> {
        return rewards.keys
    }

    fun createReward(name: String, type: RewardType, value: Any? = null): Boolean? {
        if (containsMysteryBox(name)) return null
        else if (rewards.containsKey(name) || cf.config.contains("rewards.$name")) return false
        val reward = when (type) {
            COMMAND -> Reward(listOf<String>())
            ITEM -> Reward(value as ItemStack)
            MONEY -> Reward((value?:0.0) as Double)
            MESSAGE -> Reward(type)
        }
        reward.name = name
        reward.updateMessage()
        rewards[name] = reward
        saveReward(reward)
        return true
    }

    fun setIcon(rew: String, icon: ItemStack): Boolean {
        val reward = rewards[rew] ?: return false
        reward.icon = icon
        saveReward(reward)
        return true
    }

    fun setItem(rew: String, item: ItemStack): Boolean {
        val reward = rewards[rew] ?: return false
        reward.item = item
        reward.icon = item
        saveReward(reward)
        return true
    }

    fun setMessage(rew: String, message: String): Boolean {
        val reward = rewards[rew] ?: return false
        reward.message = message
        saveReward(reward)
        return true
    }

    fun setMoney(rew: String, money: Double): Boolean {
        val reward = rewards[rew] ?: return false
        reward.money = money
        saveReward(reward)
        return true
    }

    private fun saveReward(reward: Reward) {
        cf.config.set("rewards.${reward.name}", reward)
        cf.config.set("enabled-rewards", rewards.keys.sorted().toList())
        cf.saveConfig()
    }

    fun openReward(p: Player, rew: String): Reward? {
        val reward = rewards[rew] ?: return null

        object : BukkitRunnable() {
            override fun run() {
                when (reward.rewardType) {
                    COMMAND -> reward.commands?.forEach { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.replace("{player}", p.name)) }
                    ITEM -> p.inventory.addItem(reward.item)
                    MONEY -> econ.depositPlayer(p, reward.money ?: 0.0)
                    MESSAGE -> null
                }
                val sound = Sound.entries.find { it.name.contains("LEVEL_UP") }
                p.playSound(p.location, sound!!, 0.5f, 1.2f)
            }
        }.runTaskLater(plugin, 5 * 20)

        return reward
    }

    fun forceOpenReward(p: Player, rew: String): Reward? {
        val reward = rewards[rew] ?: return null

        when (reward.rewardType) {
            COMMAND -> reward.commands?.forEach { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it.replace("{player}", p.name)) }
            ITEM -> p.inventory.addItem(reward.item)
            MONEY -> econ.depositPlayer(p, reward.money ?: 0.0)
            MESSAGE -> TODO()
        }

        return reward
    }


    fun loadRewards() {
        cf.config.getStringList("enabled-rewards").map {
            val reward = cf.config.get("rewards.$it") as Reward
            reward.name = it
            reward.updateMessage()
            rewards[it] = reward
        }
    }

    fun reloadRewards() {
        rewards.clear()
        loadRewards()
    }
}