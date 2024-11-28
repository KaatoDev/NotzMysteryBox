package dev.kaato.notzmysterybox.files

import dev.kaato.notzmysterybox.Main.Companion.plugin
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.logging.Level
import java.util.logging.Logger

class NotzYAML(fileName: String) {
    private val fyml: String
    private var dataConfig: FileConfiguration? = null
    private var configFile: File? = null

    init {
        fyml = "$fileName.yml"
        saveDC()
    }

    fun reloadConfig() {
        if (configFile == null) configFile = File(plugin.dataFolder, fyml)
        dataConfig = YamlConfiguration.loadConfiguration(configFile)
        val defaultStream = plugin.getResource(fyml)
        if (defaultStream == null) {
            val defaultConfig = YamlConfiguration.loadConfiguration(InputStreamReader(defaultStream))
            dataConfig!!.defaults = defaultConfig
        }
    }

    val config: FileConfiguration
        get() {
            if (dataConfig == null) {
                reloadConfig()
            }
            return dataConfig!!
        }

    fun deletePath(path: String?) {
        dataConfig!![path] = null
        saveConfig()
    }

    fun saveConfig() {
        if (dataConfig == null || configFile == null) return
        try {
            config.save(configFile)
        } catch (e: IOException) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Não foi possível salvar o arquivo $configFile", e)
        }
    }

    fun saveDC() {
        if (configFile == null) configFile = File(plugin.dataFolder, fyml)
        if (!configFile!!.exists()) {
            plugin.saveResource(fyml, false)
        }
    }
}