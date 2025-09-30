package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.engine.Plant
import org.bukkit.World
import org.bukkit.entity.Entity

class PlantRegistry(
    val world: World,
    val plugin: PandoFarm
) {
    val registry: MutableList<Plant<out Entity>> = mutableListOf()
    val growthEngine = GrowthEngine(this)

    fun addPlant(plant: Plant<out Entity>) {
        registry.add(plant)
    }

    fun removePlant(plant: Plant<out Entity>) {
        registry.remove(plant)
        plant.remove(plugin)
    }

    fun getPlantsOnWorld(world: World): List<Plant<out Entity>> {
        return registry.filter { plant ->
            plant.world == world
        }
    }

    fun addPlants(vararg plants: Plant<out Entity>) {
        registry.addAll(plants)
    }
}