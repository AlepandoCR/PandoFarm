package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.data.HarvestPlantDto
import com.mapachos.pandoFarm.plants.data.StaticPlantDto
import com.mapachos.pandoFarm.plants.engine.GrowthStage
import com.mapachos.pandoFarm.plants.engine.HarvestPlant
import com.mapachos.pandoFarm.plants.engine.Plant
import com.mapachos.pandoFarm.plants.engine.StaticPlant
import org.bukkit.World
import org.bukkit.entity.Entity
import java.util.*

class GlobalPlantRegistry(val plugin: PandoFarm) {
    private val allPlantsMap = HashMap<UUID, Plant<out Entity>>()

    val plantRegistries = mutableListOf<PlantRegistry>()

    fun registerPlantRegistry(plantRegistry: PlantRegistry) {
        plantRegistries.add(plantRegistry)
        plantRegistry.growthEngine.start()
    }

    fun unregisterPlantRegistry(plantRegistry: PlantRegistry) {
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
        getRegistryForWorld(plant.world).addPlant(plant)
        allPlantsMap[plant.uniqueIdentifier] = plant
    }

    fun unregisterPlant(plant: Plant<out Entity>) {
        getRegistryForWorld(plant.world).removePlant(plant)
        allPlantsMap.remove(plant.uniqueIdentifier)
    }

    fun loadPlantsOnWorld(world: World) {
        val staticDtos = plugin.getStaticPlantTable().getAll().filter { it.location.world == world.name }
        staticDtos.forEach { dto ->
            val plant = StaticPlant.load(dto)
            if (allPlantsMap[plant.uniqueIdentifier] == null) {
                registerPlant(plant)
                plant.spawn(dto.location.toLocation())
            }
        }

        val harvestDtos = plugin.getHarvestPlantTable().getAll().filter { it.location.world == world.name }
        harvestDtos.forEach { dto ->
            val plant = HarvestPlant.load(dto)
            if (allPlantsMap[plant.uniqueIdentifier] == null) {
                registerPlant(plant)
                plant.spawn(dto.location.toLocation())
            }
        }
    }

    fun removePlantsOnWorld(world: World, save: Boolean = true) {
        val registry = plantRegistries.find { it.world == world }
        if (registry != null) {
            val plantsToRemove = registry.registry.filter { it.world == world }
            if (save) {
                plantsToRemove.forEach { it.remove(plugin) }
            } else {
                plantsToRemove.forEach { it.detach() }
            }
            plantsToRemove.forEach { allPlantsMap.remove(it.uniqueIdentifier) }
            registry.registry.removeAll(plantsToRemove)
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
            plugin.getStaticPlantTable().insertBatch(staticDtos)
            plugin.getHarvestPlantTable().insertBatch(harvestDtos)
        }
        plantRegistries.forEach { reg ->
            reg.registry.forEach { plant -> plant.detach() }
            reg.growthEngine.stop()
        }
        plantRegistries.clear()
        allPlantsMap.clear()
    }
}