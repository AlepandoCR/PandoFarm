package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.plants.engine.Plant
import org.bukkit.World
import org.bukkit.entity.Entity
import java.util.UUID

object GlobalPlantRegistry {
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

    fun serveWorld(world: World) {
        if(plantRegistries.none { it.world == world }){
            registerPlantRegistry(PlantRegistry(world))
        }
    }

    fun removeWorld(world: World) {
        plantRegistries.firstOrNull { it.world == world }?.let {
            unregisterPlantRegistry(it)
        }
    }

    fun getPlantsInWorld(world: World): List<Plant<out Entity>> {
        return plantRegistries.filter { it.world == world }.flatMap { it.registry }
    }

    fun getRegistryForWorld(world: World): PlantRegistry? {
        return plantRegistries.firstOrNull { it.world == world }
    }


    fun getPlant(uuid: UUID): Plant<out Entity>? {
        return getAllPlants().firstOrNull { it.uniqueIdentifier == uuid }
    }
}