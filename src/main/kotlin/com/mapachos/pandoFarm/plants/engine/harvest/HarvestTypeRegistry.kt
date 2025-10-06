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
        plugin.logger.info("[HarvestTypeRegistry] Starting load of harvest types...")
        loadFromFolder()
        plugin.logger.info("[HarvestTypeRegistry] Loaded ${harvests.size} harvest types on startup.")
        plugin.getCommand("reloadharvests")?.setExecutor(getReloadCommand())
    }

    fun registerHarvest(harvest: HarvestType) {
        if(harvests.any { it.name.equals(harvest.name, true) }){
            plugin.logger.warning("[HarvestTypeRegistry] Duplicate harvest type '${harvest.name}' ignored.")
            return
        }
        harvests.add(harvest)
        plugin.logger.fine("[HarvestTypeRegistry] Registered harvest type '${harvest.name}'.")
    }

    fun unregisterHarvest(harvest: HarvestType) {
        harvests.remove(harvest)
        plugin.logger.fine("[HarvestTypeRegistry] Unregistered harvest type '${harvest.name}'.")
    }

    fun getHarvestByName(name: String): HarvestType? = harvests.firstOrNull { it.name == name }

    fun reload() {
        plugin.logger.info("[HarvestTypeRegistry] Reload requested. Clearing ${harvests.size} current harvest types...")
        harvests.clear()
        loadFromFolder()
        // PlantTypeRegistry depende de harvest types, recargarlos después
        PlantTypeRegistry.reload()
        plugin.logger.info("[HarvestTypeRegistry] Reload complete. Now registered: ${harvests.size} harvest types.")
    }

    fun getReloadCommand(): CommandExecutor = autoCommand({ sender, _, _, _ ->
        reload()
        sender.sendMessage("§3Harvest types reloaded. (${harvests.size})")
        true
    }, onlyOp = true, minArgs = 0, maxArgs = 0, usage = "/reloadharvests")

    private fun loadFromFolder() {
        val folder = getHarvestDataFolder()
        if(!folder.exists()){
            plugin.logger.warning("[HarvestTypeRegistry] Folder '${folder.path}' does not exist; creating.")
            folder.mkdirs()
        }
        val files = folder.listFiles()?.filter { it.extension.equals("yml", true) } ?: emptyList()
        if(files.isEmpty()){
            plugin.logger.warning("[HarvestTypeRegistry] No harvest type definition files found in '${folder.path}'.")
            return
        }
        plugin.logger.info("[HarvestTypeRegistry] Loading ${files.size} harvest type file(s)...")
        var loaded = 0
        files.forEach { file ->
            val fileName = file.name
            try {
                val auto = autoYml<HarvestTypeDto>(fileName, harvestDataFolder)
                val dto = auto.load()
                if(dto == null){
                    plugin.logger.warning("[HarvestTypeRegistry] File '$fileName' could not be parsed (null DTO).")
                    return@forEach
                }
                val harvestType = dto.toHarvestType()
                registerHarvest(harvestType)
                loaded++
            } catch (ex: Exception){
                plugin.logger.severe("[HarvestTypeRegistry] Failed loading '$fileName': ${ex.message}")
            }
        }
        plugin.logger.info("[HarvestTypeRegistry] Finished loading harvest types. Loaded=$loaded / TotalFiles=${files.size} RegisteredNow=${harvests.size}")
    }

    private fun getHarvestDataFolder() : File {
        val folder = plugin.dataFolder.resolve(harvestDataFolder.path)
        if (!folder.exists()) folder.mkdir()
        return folder
    }
}