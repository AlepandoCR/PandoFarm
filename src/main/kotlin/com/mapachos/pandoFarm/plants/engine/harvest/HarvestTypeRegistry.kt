package com.mapachos.pandoFarm.plants.engine.harvest

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.engine.PlantTypeRegistry
import com.mapachos.pandoFarm.plants.engine.harvest.data.HarvestTypeDto
import com.mapachos.pandoFarm.util.autoCommand
import com.mapachos.pandoFarm.util.autoYml
import com.mapachos.pandoFarm.util.yml.DataFolder
import org.bukkit.command.CommandExecutor
import java.io.File

object HarvestTypeRegistry {

    val harvestDataFolder = DataFolder.HARVESTS

    val plugin = PandoFarm.getInstance()

    val harvests = mutableListOf<HarvestType>()

    fun start() {
        loadFromFolder()
        plugin.getCommand("reloadharvests")?.setExecutor(getReloadCommand())
    }

    fun registerHarvest(harvest: HarvestType) {
        harvests.add(harvest)
    }

    fun unregisterHarvest(harvest: HarvestType) {
        harvests.remove(harvest)
    }

    fun getHarvestByName(name: String): HarvestType? {
        return harvests.firstOrNull { it.name == name }
    }

    fun reload() {
        harvests.clear()
        loadFromFolder()
    }

    fun getReloadCommand(): CommandExecutor {
        return autoCommand({ sender, _, _, _ ->
            PlantTypeRegistry.reload()
            sender.sendMessage("§Harvest types reloaded.")
            true
        }, onlyOp = true)
    }

    private fun loadFromFolder() {
        val folder = getHarvestDataFolder()
        folder.listFiles()?.forEach { file ->
            val autoYml = autoYml<HarvestTypeDto>(file.name, harvestDataFolder)
            autoYml.load()?.let { registerHarvest(it.toHarvestType()) }
        }
    }

    private fun getHarvestDataFolder() : File {
        val folder = plugin.dataFolder.resolve(harvestDataFolder.path)

        if (!folder.exists()) {
            folder.mkdir()
        }

        return folder
    }
}