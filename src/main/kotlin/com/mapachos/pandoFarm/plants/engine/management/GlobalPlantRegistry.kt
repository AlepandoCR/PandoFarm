package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.engine.GrowthStage
import com.mapachos.pandoFarm.plants.engine.Plant
import org.bukkit.World
import org.bukkit.entity.Entity
import java.util.UUID

class GlobalPlantRegistry(val plugin: PandoFarm) {
    val plantRegistries = mutableListOf<PlantRegistry>()

    fun registerPlantRegistry(plantRegistry: PlantRegistry) {
        plantRegistries.add(plantRegistry)
    }

    fun unregisterPlantRegistry(plantRegistry: PlantRegistry) {
        plantRegistries.remove(plantRegistry)
    }

    fun getAllPlants(): List<Plant<out Entity>> {
        return plantRegistries.flatMap { it.registry }
    }

    private fun serveWorld(world: World) : PlantRegistry{
        val registry = PlantRegistry(world, plugin)
        registerPlantRegistry(registry)
        return registry
    }

    fun removeWorld(world: World) {
        plantRegistries.firstOrNull { it.world == world }?.let {
            unregisterPlantRegistry(it)
            it.removePlantsOnWorld(world)
        }
    }

    fun getPlantsInWorld(world: World): List<Plant<out Entity>> {
        return plantRegistries.filter { it.world == world }.flatMap { it.registry }
    }

    fun getRegistryForWorld(world: World): PlantRegistry {
        return plantRegistries.find { it.world == world } ?: serveWorld(world)
    }

    fun getPlant(uuid: UUID): Plant<out Entity>? {
        return getAllPlants().find { it.uniqueIdentifier == uuid }
    }

    fun getPlantsByType(type: PlantType<out Entity>): List<Plant<out Entity>> {
        return getAllPlants().filter { it.plantType == type }
    }

    fun getPlantsByStage(stage: GrowthStage): List<Plant<out Entity>> {
        return getAllPlants().filter { it.stage == stage }
    }
}