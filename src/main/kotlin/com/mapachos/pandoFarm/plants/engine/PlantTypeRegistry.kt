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
        plugin.logger.info("[PlantTypeRegistry] Starting load of plant types...")
        loadFromFolder()
        plugin.logger.info("[PlantTypeRegistry] Loaded ${plants.size} plant types on startup.")
        plugin.getCommand("reloadplants")?.setExecutor(getReloadCommand())
    }

    fun registerPlantType(plantType: PlantType<out Entity>) {
        if(plants.any { it.name.equals(plantType.name, true) }){
            plugin.logger.warning("[PlantTypeRegistry] Duplicate plant type name '${plantType.name}' ignored.")
            return
        }
        plants.add(plantType)
        plugin.logger.fine("[PlantTypeRegistry] Registered plant type '${plantType.name}'.")
    }

    fun unregisterPlantType(plantType: PlantType<out Entity>) {
        plants.remove(plantType)
        plugin.logger.fine("[PlantTypeRegistry] Unregistered plant type '${plantType.name}'.")
    }

    fun getPlantTypeByName(name: String): PlantType<out Entity>? = plants.firstOrNull { it.name == name }

    fun reload() {
        plugin.logger.info("[PlantTypeRegistry] Reload requested. Clearing ${plants.size} current plant types...")
        plants.clear()
        loadFromFolder()
        plugin.logger.info("[PlantTypeRegistry] Reload complete. Now registered: ${plants.size} plant types.")
    }

    private fun loadFromFolder() {
        val folder = getPlantDataFolder()
        if(!folder.exists()){
            plugin.logger.warning("[PlantTypeRegistry] Folder '${folder.path}' does not exist; creating.")
            folder.mkdirs()
        }
        val files = folder.listFiles()?.filter { it.extension.equals("yml", true) } ?: emptyList()
        if(files.isEmpty()){
            plugin.logger.warning("[PlantTypeRegistry] No plant type definition files found in '${folder.path}'.")
            return
        }
        plugin.logger.info("[PlantTypeRegistry] Loading ${files.size} plant type file(s)...")
        var loaded = 0
        files.forEach { file ->
            val fileName = file.name
            try {
                val auto = autoYml<PlantTypeDto>(fileName, plantDataFolder)
                val dto = auto.load()
                if(dto == null){
                    plugin.logger.warning("[PlantTypeRegistry] File '$fileName' could not be parsed (null DTO).")
                    return@forEach
                }
                val type = dto.toPlantType()
                registerPlantType(type)
                loaded++
            } catch (ex: Exception){
                plugin.logger.severe("[PlantTypeRegistry] Failed loading '$fileName': ${ex.message}")
            }
        }
        plugin.logger.info("[PlantTypeRegistry] Finished loading plant types. Loaded=$loaded / TotalFiles=${files.size} RegisteredNow=${plants.size}")
    }

    fun getReloadCommand(): CommandExecutor = autoCommand({ sender, _, _, _ ->
        reload()
        sender.sendMessage("§3Plant types reloaded. (${plants.size})")
        true
    }, onlyOp = true)

    private fun getPlantDataFolder() : File {
        val folder = plugin.dataFolder.resolve(plantDataFolder.path)
        if (!folder.exists()) folder.mkdir()
        return folder
    }
}