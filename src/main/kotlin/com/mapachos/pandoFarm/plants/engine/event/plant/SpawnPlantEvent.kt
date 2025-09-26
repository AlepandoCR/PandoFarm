package com.mapachos.pandoFarm.plants.engine.event.plant

import com.mapachos.pandoFarm.plants.engine.Plant
import org.bukkit.entity.Entity

class SpawnPlantEvent<E: Entity>(plant: Plant<E>): PlantEvent<E>(plant) {
}