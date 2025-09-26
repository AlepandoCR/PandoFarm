package com.mapachos.pandoFarm.plants.engine.event.plant

import com.mapachos.pandoFarm.plants.engine.Plant
import com.mapachos.pandoFarm.plants.engine.event.FarmEvent
import org.bukkit.entity.Entity

abstract class PlantEvent<E: Entity>(val plant: Plant<E>): FarmEvent() {
}