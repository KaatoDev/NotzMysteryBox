package dev.kaato.notzmysterybox.entities

import dev.kaato.notzmysterybox.entities.enums.RewardType
import dev.kaato.notzmysterybox.entities.enums.RewardType.*
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack

class Reward(val rewardType: RewardType, var icon: ItemStack = ItemStack(Material.STONE)) : ConfigurationSerializable {
    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any>): Reward {
            try {
                val rewardType = RewardType.valueOf(args["rewardType"] as String)
                val message = args["message"] as String
                val icon = args["icon"] as ItemStack
                var commands: List<String> = listOf()
                var item: ItemStack? = null
                var money: Double? = 0.0

                val result = when (rewardType) {
                    COMMAND -> {
                        commands = args["commands"] as List<String>
                        Reward(commands, icon)
                    }

                    ITEM -> {
                        item = args["item"] as ItemStack
                        Reward(item)
                    }

                    MONEY -> {
                        money = args["money"] as Double
                        Reward(money, icon)
                    }

                    MESSAGE -> Reward(message, icon)
                }

                return result;
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    constructor(commands: List<String>, icon: ItemStack = ItemStack(Material.GRASS)) : this(COMMAND, icon) {
        this.commands = commands
    }

    constructor(item: ItemStack) : this(ITEM, item) {
        this.item = item
    }

    constructor(money: Double, icon: ItemStack = ItemStack(Material.DIRT)) : this(MONEY, icon) {
        this.money = money
    }

    constructor(message: String, icon: ItemStack = ItemStack(Material.PAPER)) : this(MESSAGE, icon) {
        this.message = message
    }

    var name = ""
    var message = "Recompensa $name recebida!"
    var commands: List<String>? = null
    var item: ItemStack? = null
    var money: Double? = null

    fun updateMessage() {
        message = "Recompensa $name recebida!"
    }

    override fun serialize(): Map<String?, Any?>? {
        val result = LinkedHashMap<String, Any>()
        result["rewardType"] = rewardType.toString()
        result["message"] = message
        result["icon"] = icon

        commands?.let { result["commands"] = it }
        item?.let { result["item"] = it }
        money?.let { result["money"] = it }

        return result.toMap();
    }
}