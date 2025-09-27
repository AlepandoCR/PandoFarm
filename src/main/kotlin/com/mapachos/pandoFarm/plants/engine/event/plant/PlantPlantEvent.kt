package com.mapachos.pandoFarm.plants.engine.event.plant

import com.mapachos.pandoFarm.plants.engine.Plant
import com.mapachos.pandoFarm.plants.engine.event.FarmEvent
import org.bukkit.entity.Entity

class PlantPlantEvent(val plant: Plant<out Entity>, val gardener: Entity) : FarmEvent() {

}