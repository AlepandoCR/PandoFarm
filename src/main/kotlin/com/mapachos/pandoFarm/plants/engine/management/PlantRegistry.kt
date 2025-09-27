package com.mapachos.pandoFarm.plants.engine.management

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.plants.engine.HarvestPlant
import com.mapachos.pandoFarm.plants.engine.Plant
import com.mapachos.pandoFarm.plants.engine.StaticPlant
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
        plant.save()
    }

    fun getPlantsOnWorld(world: World): List<Plant<out Entity>> {
        return registry.filter { plant ->
            plant.world == world
        }
    }

    fun removePlantsOnWorld(world: World, save: Boolean = true) {
        val filteredList = registry.filter { it.world == world }
        if(save){
            filteredList.forEach { it.remove() }
        }
        registry.removeAll(filteredList)
    }

    fun addPlants(vararg plants: Plant<out Entity>) {
        registry.addAll(plants)
    }

    fun loadPlantsOnWorld(world: World) {

        val staticDtos = plugin.getStaticPlantTable().getAll().filter { it.location.world == world.name }
        staticDtos.forEach { dto ->
            val plant = StaticPlant.load(dto)
            if(!registry.contains(plant)) {
                addPlant(plant)
                plant.spawn(dto.location.toLocation())
            }

        }

        val harvestDtos = plugin.getHarvestPlantTable().getAll().filter { it.location.world == world.name }
        harvestDtos.forEach { dto ->
            val plant = HarvestPlant.load(dto)
            if(!registry.contains(plant)) {
                addPlant(plant)
                plant.spawn(dto.location.toLocation())
            }
        }
    }
}