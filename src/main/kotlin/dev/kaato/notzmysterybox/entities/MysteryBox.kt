package dev.kaato.notzmysterybox.entities

import dev.kaato.notzmysterybox.utils.MessageU
import dev.kaato.notzmysterybox.utils.MessageU.c
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class MysteryBox(val display: String = "&6Caixa Misteriosa", val description: List<String> = listOf(), val type: Material = Material.CHEST, val isEnchanted: Boolean = true, val rewards: HashMap<String, Double> = hashMapOf()) : ConfigurationSerializable {
    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): MysteryBox {
            val display = args["display"] as String
            val description = args["description"] as List<String>
            val type = Material.getMaterial(args["type"] as String) ?: Material.CHEST
            var isEnchanted = (args["isEnchanted"] ?: false) as Boolean
            var rewards = args["rewards"] as HashMap<String, Double>

            val result = MysteryBox(display, description, type, isEnchanted, rewards)

            return result;
        }
    }

    init {
        updateBox()
    }

    private lateinit var box: ItemStack
    var name = ""

    fun updateBox() {
        box = ItemStack(type)
        val meta = box.itemMeta
        meta.displayName = c(display)

        if (description.isNotEmpty())
            meta.lore = description.map(MessageU::c)

        if (isEnchanted) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        box.itemMeta = meta
    }

    fun getItem(): ItemStack {
        return box.clone()
    }

    override fun serialize(): Map<String?, Any?>? {
        val result = LinkedHashMap<String, Any>()
        result["display"] = display
        result["description"] = description
        result["type"] = type.name
        result["isEnchanted"] = isEnchanted
        result["rewards"] = rewards.toSortedMap()

        return result.toMap();
    }
//    val sound: Sound =
//        try {
//            Sound.valueOf("BLOCK_NOTE_PLING")
//        } catch (e: Exception) {
//            Sound.valueOf("NOTE_PLING")
//        }
}