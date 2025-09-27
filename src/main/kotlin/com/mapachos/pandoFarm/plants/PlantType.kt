package com.mapachos.pandoFarm.plants

import com.mapachos.pandoFarm.model.plant.PlantModelBatch
import com.mapachos.pandoFarm.plants.data.PlantTypeDto
import com.mapachos.pandoFarm.plants.engine.InteractionMethod
import org.bukkit.entity.Entity

class PlantType<E: Entity>(val name: String, val harvestMethod: InteractionMethod, val interactionMethod: InteractionMethod, val modelBatch: PlantModelBatch<E>, val matureAge: Long) {
    fun toDto(): PlantTypeDto {
        return PlantTypeDto(name, harvestMethod.name, interactionMethod.name, modelBatch.toDto(), matureAge)
    }
}