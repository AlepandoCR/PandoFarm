package com.mapachos.pandoFarm.plants.engine

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.data.PlantTypeDto
import com.mapachos.pandoFarm.util.autoCommand
import com.mapachos.pandoFarm.util.autoYml
import com.mapachos.pandoFarm.util.yml.DataFolder
import org.bukkit.command.CommandExecutor
import org.bukkit.entity.Entity
import java.io.File

object PlantTypeRegistry {

    val plantDataFolder = DataFolder.PLANTS

    val plugin = PandoFarm.getInstance()

    val plants = mutableListOf<PlantType<out Entity>>()

    fun start() {
        loadFromFolder()
        plugin.getCommand("reloadplants")?.setExecutor(getReloadCommand())
    }

    fun registerPlantType(harvest: PlantType<out Entity>) {
        plants.add(harvest)
    }

    fun unregisterPlantType(harvest: PlantType<out Entity>) {
        plants.remove(harvest)
    }

    fun getPlantTypeByName(name: String): PlantType<out Entity>? {
        return plants.firstOrNull { it.name == name }
    }

    fun reload() {
        plants.clear()
        loadFromFolder()
    }

    private fun loadFromFolder() {
        val folder = getPlantDataFolder()
        folder.listFiles()?.forEach { file ->
            val autoYml = autoYml<PlantTypeDto>(file.name, plantDataFolder)
            autoYml.load()?.let { registerPlantType(it.toPlantType()) }
        }
    }

    fun getReloadCommand(): CommandExecutor {
        return autoCommand({ sender, _, _, _ ->
            reload()
            sender.sendMessage("§3Plant types reloaded.")
            true
        }, onlyOp = true)
    }

    private fun getPlantDataFolder() : File {
        val folder = plugin.dataFolder.resolve(plantDataFolder.path)

        if (!folder.exists()) {
            folder.mkdir()
        }

        return folder
    }
}