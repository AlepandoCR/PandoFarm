package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.plants.engine.Plant
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.entity.Entity

class PlantRegistry(val world: World) {
    val registry: MutableList<Plant<out Entity>> = mutableListOf()

    fun addPlant(plant: Plant<out Entity>) {
        registry.add(plant)
    }

    fun removePlant(plant: Plant<out Entity>) {
        registry.remove(plant)
        plant.save()
    }

    fun getPlantsOnChunk(chunk: Chunk): List<Plant<out Entity>> {
        return registry.filter { plant ->
            plant.chunk == chunk
        }
    }

    fun removePlantsOnChunk(chunk: Chunk, save: Boolean = true) {
        val filteredList = registry.filter { it.chunk == chunk }
        if(save){
            filteredList.forEach { it.save() }
        }
        registry.removeAll(filteredList)
    }

    fun addPlants(vararg plants: Plant<out Entity>) {
        registry.addAll(plants)
    }

    fun loadPlantsOnChunk(chunk: Chunk) {
        TODO("get entities from chunk and load plants from persistent data containers of entities")
    }

    fun loadPlants() {
        TODO("load all plants from persistent data containers of entities in the world")
    }
}