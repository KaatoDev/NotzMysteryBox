package dev.kaato.notzmysterybox

import dev.kaato.notzmysterybox.commands.NMysteryBoxC
import dev.kaato.notzmysterybox.entities.MysteryBox
import dev.kaato.notzmysterybox.entities.Reward
import dev.kaato.notzmysterybox.events.CancelInventoryEv
import dev.kaato.notzmysterybox.events.OpenMysteryBoxEv
import dev.kaato.notzmysterybox.events.PreventMysteryBox
import dev.kaato.notzmysterybox.files.NotzYAML
import dev.kaato.notzmysterybox.managers.NotzManager.loadPlugin
import dev.kaato.notzmysterybox.utils.MessageU.c
import dev.kaato.notzmysterybox.utils.MessageU.send
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class Main : JavaPlugin() {
    companion object {
        lateinit var econ: Economy
        lateinit var plugin: JavaPlugin
        lateinit var cf: NotzYAML
        lateinit var prefix: String
        var started: Boolean = false
    }

    override fun onEnable() {
        plugin = this
        ConfigurationSerialization.registerClass(MysteryBox::class.java, "MysteryBox")
        ConfigurationSerialization.registerClass(Reward::class.java, "Reward")
        cf = NotzYAML("config")
        getPluginManager().registerEvents(PreventMysteryBox(), this)

        object : BukkitRunnable() {
            override fun run() {
                start()
            }
        }.runTaskLater(this, 2 * 20L)
    }

    fun regCommands() {
        this.getCommand("nmysterybox")?.executor = NMysteryBoxC()
    }

    fun regEvents() {
        getPluginManager().registerEvents(OpenMysteryBoxEv(), this)
        getPluginManager().registerEvents(CancelInventoryEv(), this)
    }

    fun regTabs() {
        getCommand("nmysterybox")?.tabCompleter = NMysteryBoxC()
    }

    private fun start() {
        regCommands()
        regEvents()
        regTabs()
        letters()
        startVault()
        loadPlugin()
        started = true
    }

    private fun letters() {
        val site = "https://kaato.dev/plugins"
        send(
            Bukkit.getConsoleSender(),
            """
                &2Inicializado com sucesso.
                &f┳┓    &5┳┳┓          ┳┓    
                &f┃┃┏┓╋┓&5┃┃┃┓┏┏╋┏┓┏┓┓┏┣┫┏┓┓┏
                &f┛┗┗┛┗┗&5┛ ┗┗┫┛┗┗ ┛ ┗┫┻┛┗┛┛┗
                &f      &5    ┛       ┛      
                
                $prefix &6Para mais plugins como este, acesse &b$site &6!!
                
            """.trimIndent()
        )
        Bukkit.getOnlinePlayers().forEach {
            if (it.hasPermission("notzmysterybox.admin")) {
                it.sendMessage(c("&5[&fNotzMysterybox&5] &aInicializado com sucesso."))
            }
        }
    }

    private fun startVault() {
        if (!setupEconomy()) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", description.name))
            server.pluginManager.disablePlugin(this)
            return
        }
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null)
            return false

        val rsp: RegisteredServiceProvider<Economy> = server.servicesManager.getRegistration(Economy::class.java)

        econ = rsp.provider
        return true
    }

    override fun onDisable() {
    }
}
