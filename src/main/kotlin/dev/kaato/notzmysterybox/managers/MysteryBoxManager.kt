package dev.kaato.notzmysterybox.managers

import dev.kaato.notzmysterybox.Main.Companion.cf
import dev.kaato.notzmysterybox.entities.MysteryBox
import dev.kaato.notzmysterybox.managers.RewardsManager.containsReward
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.HashMap
import kotlin.random.Random

object MysteryBoxManager {
    private val mysteryBoxes = hashMapOf<String, MysteryBox>()
    private val registeredMysteryBoxesTypes = mutableListOf<Material>()

    fun containsMysteryBox(mb: String): Boolean {
        return mysteryBoxes.containsKey(mb)
    }

    fun getMysteryBox(mb: String): MysteryBox? {
        return mysteryBoxes[mb]
    }

    fun getMysteryBoxList(): MutableSet<String> {
        return mysteryBoxes.keys
    }

    fun getMysteryBoxTypes(): Array<Material> {
        return registeredMysteryBoxesTypes.toTypedArray()
    }

    fun createMysteryBox(name: String, isEnchanted: Boolean): Boolean? {
        if (containsReward(name)) return null
        else if (mysteryBoxes.containsKey(name) || cf.config.contains("mysteryboxes.$name")) return false
        val mysteryBox = MysteryBox(name, isEnchanted = isEnchanted)
        mysteryBox.name = name
        mysteryBoxes[name] = mysteryBox
        saveMysteryBox(mysteryBox)
        return true
    }

    fun isMysteryBox(item: ItemStack): MysteryBox? {
        val mb = item.clone()
        return mysteryBoxes.values.find { it.getItem().isSimilar(mb) }
    }

    fun removeReward(mb: String, rew: String): Boolean? {
        val mysteryBox = mysteryBoxes[mb] ?: return null
        mysteryBox.rewards.remove(rew) ?: return false
        saveMysteryBox(mysteryBox)
        return true
    }

    fun addReward(mb: String, rew: String, chance: Double): Boolean? {
        val mysteryBox = mysteryBoxes[mb] ?: return null
        mysteryBox.rewards.let {
            if (!it.containsKey(rew)) it[rew] = chance
            else return false
        }
        saveMysteryBox(mysteryBox)
        return true
    }

    fun setReward(mb: String, rew: String, chance: Double): Int {
        val mysteryBox = mysteryBoxes[mb] ?: return 0
        mysteryBox.rewards.let {
            if (it.containsKey(rew)) {
                if (it[rew] != chance) it[rew] = chance
                else return 1
            } else return 2
        }
        saveMysteryBox(mysteryBox)
        return 3
    }

    private fun saveMysteryBox(mysteryBox: MysteryBox) {
        cf.config.set("mysteryboxes.${mysteryBox.name}", mysteryBox)
        cf.config.set("enabled-mysteryboxes", mysteryBoxes.keys.sorted().toList())
        cf.saveConfig()
    }

    fun viewRewardsMB(mb: String): HashMap<String, Double>? {
        val mysteryBox = mysteryBoxes[mb] ?: return null
        return mysteryBox.rewards
    }

    fun getMysteryBoxReward(mysteryBox: MysteryBox): String? {
        if (mysteryBox.rewards.isEmpty) return null

        var cres = Random.nextDouble(mysteryBox.rewards.values.sum())

//        println(mysteryBox.rewards)

        val chances = mysteryBox.rewards.toSortedMap(Comparator<String> { a, b ->
            val v1 = mysteryBox.rewards[a] ?: 0.0
            val v2 = mysteryBox.rewards[b] ?: 0.0

            if (v1 == v2) a.compareTo(b)
            else v1.compareTo(v2)
        })

        var c = 0.0

        for (rew: String in chances.keys) {
            val ch = c + (chances[rew] ?: 0.0)
            c = ch

            if (cres <= ch) return rew
        }

        return null
    }

    fun loadMysteryBoxes() {
        cf.config.getStringList("enabled-mysteryboxes").forEach {
            val box = cf.config.get("mysteryboxes.$it") as MysteryBox
            box.name = it
            mysteryBoxes[it] = box
            registeredMysteryBoxesTypes.add(box.type)
        }
    }

    fun reloadMysteryBoxes() {
        mysteryBoxes.clear()
        loadMysteryBoxes()
    }
}