package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.data.HarvestPlantDto
import com.mapachos.pandoFarm.plants.data.StaticPlantDto
import com.mapachos.pandoFarm.plants.engine.GrowthStage
import com.mapachos.pandoFarm.plants.engine.HarvestPlant
import com.mapachos.pandoFarm.plants.engine.Plant
import com.mapachos.pandoFarm.plants.engine.StaticPlant
import com.mapachos.pandoFarm.util.later
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.entity.Entity
import java.util.*

class GlobalPlantRegistry(val plugin: PandoFarm) {
    private val allPlantsMap = HashMap<UUID, Plant<out Entity>>()

    val plantRegistries = mutableListOf<PlantRegistry>()

    fun registerPlantRegistry(plantRegistry: PlantRegistry) {
        plantRegistries.add(plantRegistry)
        plugin.logger.info("[GlobalPlantRegistry] Registered PlantRegistry for world=${plantRegistry.world.name}")
        plantRegistry.growthEngine.start()
    }

    fun unregisterPlantRegistry(plantRegistry: PlantRegistry) {
        plugin.logger.info("[GlobalPlantRegistry] Unregister PlantRegistry for world=${plantRegistry.world.name}")
        plantRegistry.growthEngine.stop()
        plantRegistries.remove(plantRegistry)
    }

    fun getAllPlants(): List<Plant<out Entity>> = allPlantsMap.values.toList()

    private fun serveWorld(world: World) : PlantRegistry{
        val registry = PlantRegistry(world, plugin)
        registerPlantRegistry(registry)
        return registry
    }

    fun getPlantsInWorld(world: World): List<Plant<out Entity>> = plantRegistries.filter { it.world == world }.flatMap { it.registry }

    fun getRegistryForWorld(world: World): PlantRegistry = plantRegistries.find { it.world == world } ?: serveWorld(world)

    fun getPlant(uuid: UUID): Plant<out Entity>? = allPlantsMap[uuid]

    fun getPlantsByType(type: PlantType<out Entity>): List<Plant<out Entity>> = allPlantsMap.values.filter { it.plantType == type }

    fun getPlantsByStage(stage: GrowthStage): List<Plant<out Entity>> = allPlantsMap.values.filter { it.stage == stage }

    fun registerPlant(plant: Plant<out Entity>) {
        // Register plant into the world registry and global map
        val worldRegistry = getRegistryForWorld(plant.world)
        if (!worldRegistry.registry.contains(plant)) {
            worldRegistry.addPlant(plant)
        }
        allPlantsMap[plant.uniqueIdentifier] = plant
        plugin.logger.info("[GlobalPlantRegistry] Registered plant id=${plant.uniqueIdentifier} type=${plant.plantType.name} world=${plant.world.name} at=(${plant.location.blockX},${plant.location.blockY},${plant.location.blockZ})")
    }

    fun removePlant(plant: Plant<out Entity>, registry: PlantRegistry? = null) {
        // Persist and remove visual entity
        plant.remove(plugin)

        // Remove from global maps
        allPlantsMap.remove(plant.uniqueIdentifier)

        // Remove from world registry without calling remove() again
        val worldRegistry = registry ?: getRegistryForWorld(plant.world)
        worldRegistry.removePlant(plant)
        plugin.logger.info("[GlobalPlantRegistry] Removed plant id=${plant.uniqueIdentifier} type=${plant.plantType.name} world=${plant.world.name} at=(${plant.location.blockX},${plant.location.blockY},${plant.location.blockZ})")
    }

    fun loadPlantsOnWorld(world: World) {
        plugin.logger.info("[GlobalPlantRegistry] Loading plants for world=${world.name}")
        val staticDtos = plugin.getStaticPlantTable().getAll().filter { it.location.world == world.name }
        staticDtos.forEach { dto ->
            val plant = StaticPlant.load(dto)
            if (allPlantsMap[plant.uniqueIdentifier] == null) {
                // Only spawn; registration is handled by PlantSpawnEvent listener
                later({
                    plant.spawn(dto.location.toLocation())
                    plugin.logger.info("[GlobalPlantRegistry] Spawned static plant id=${plant.uniqueIdentifier} at=(${dto.location.x},${dto.location.y},${dto.location.z}) chunk=(${dto.location.toLocation().chunk.x},${dto.location.toLocation().chunk.z})")
                }, 25L)
            } else {
                plugin.logger.info("[GlobalPlantRegistry] Skipped spawn (already loaded) static plant id=${plant.uniqueIdentifier}")
            }
        }

        val harvestDtos = plugin.getHarvestPlantTable().getAll().filter { it.location.world == world.name }
        harvestDtos.forEach { dto ->
            val plant = HarvestPlant.load(dto)
            if (allPlantsMap[plant.uniqueIdentifier] == null) {
                // Only spawn; registration is handled by PlantSpawnEvent listener
                later({
                    plant.spawn(dto.location.toLocation())
                    plugin.logger.info("[GlobalPlantRegistry] Spawned harvest plant id=${plant.uniqueIdentifier} at=(${dto.location.x},${dto.location.y},${dto.location.z}) chunk=(${dto.location.toLocation().chunk.x},${dto.location.toLocation().chunk.z})")
                },25L)

            } else {
                plugin.logger.info("[GlobalPlantRegistry] Skipped spawn (already loaded) harvest plant id=${plant.uniqueIdentifier}")
            }
        }
    }

    fun removePlantsOnChunk(chunk: Chunk) {
        val world = chunk.world
        val registry = plantRegistries.find { it.world == world }
        if (registry != null) {
            val plantsToRemove = registry.registry.filter { it.location.chunk == chunk }
            plugin.logger.info("[GlobalPlantRegistry] Removing ${plantsToRemove.size} plants on chunk=(${chunk.x},${chunk.z}) world=${world.name}")
            plantsToRemove.forEach {
                removePlant(it, registry)
            }
        } else {
            plugin.logger.info("[GlobalPlantRegistry] No registry found for world=${world.name} when removing chunk=(${chunk.x},${chunk.z})")
        }
    }

    fun loadPlantsOnChunk(chunk: Chunk) {
        val world = chunk.world
        plugin.logger.info("[GlobalPlantRegistry] Loading plants on chunk=(${chunk.x},${chunk.z}) world=${world.name}")
        val staticDtos = plugin.getStaticPlantTable().getAll()
            .filter { it.location.world == world.name && it.location.toLocation().chunk.x == chunk.x && it.location.toLocation().chunk.z == chunk.z }
        staticDtos.forEach { dto ->
            val plant = StaticPlant.load(dto)
            if (allPlantsMap[plant.uniqueIdentifier] == null) {
                // Only spawn; registration is handled by PlantSpawnEvent listener
                plant.spawn(dto.location.toLocation())
                plugin.logger.info("[GlobalPlantRegistry] Spawned static plant on chunk id=${plant.uniqueIdentifier} at=(${dto.location.x},${dto.location.y},${dto.location.z})")
            } else {
                plugin.logger.info("[GlobalPlantRegistry] Skipped chunk spawn (already loaded) static plant id=${plant.uniqueIdentifier}")
            }
        }

        val harvestDtos = plugin.getHarvestPlantTable().getAll()
            .filter { it.location.world == world.name && it.location.toLocation().chunk.x == chunk.x && it.location.toLocation().chunk.z == chunk.z }
        harvestDtos.forEach { dto ->
            val plant = HarvestPlant.load(dto)
            if (allPlantsMap[plant.uniqueIdentifier] == null) {
                // Only spawn; registration is handled by PlantSpawnEvent listener
                plant.spawn(dto.location.toLocation())
                plugin.logger.info("[GlobalPlantRegistry] Spawned harvest plant on chunk id=${plant.uniqueIdentifier} at=(${dto.location.x},${dto.location.y},${dto.location.z})")
            } else {
                plugin.logger.info("[GlobalPlantRegistry] Skipped chunk spawn (already loaded) harvest plant id=${plant.uniqueIdentifier}")
            }
        }
    }

    fun removePlantsOnWorld(world: World) {
        val registry = plantRegistries.find { it.world == world }
        if (registry != null) {
            val plantsToRemove = registry.registry.filter { it.world == world }
            plugin.logger.info("[GlobalPlantRegistry] Removing ${plantsToRemove.size} plants on world=${world.name}")
            plantsToRemove.forEach { removePlant(it, registry) }
        } else {
            plugin.logger.info("[GlobalPlantRegistry] No registry found for world=${world.name} when removing all")
        }
    }

    private fun collectStaticDtos(): List<StaticPlantDto> = allPlantsMap.values.filterIsInstance<StaticPlant<out Entity>>()
        .map { it.toDto() as StaticPlantDto }

    private fun collectHarvestDtos(): List<HarvestPlantDto> = allPlantsMap.values.filterIsInstance<HarvestPlant<out Entity>>()
        .map { it.toDto() as HarvestPlantDto }

    fun shutdown(save: Boolean = true){
        if(save){
            // Batch save
            val staticDtos = collectStaticDtos()
            val harvestDtos = collectHarvestDtos()
            plugin.logger.info("[GlobalPlantRegistry] Shutdown: saving static=${staticDtos.size}, harvest=${harvestDtos.size}")
            plugin.getStaticPlantTable().insertBatch(staticDtos)
            plugin.getHarvestPlantTable().insertBatch(harvestDtos)
        }
        plantRegistries.forEach { reg ->
            reg.registry.forEach { plant -> plant.detach() }
            reg.growthEngine.stop()
        }
        plantRegistries.clear()
        allPlantsMap.clear()
        plugin.logger.info("[GlobalPlantRegistry] Shutdown complete")
    }
}