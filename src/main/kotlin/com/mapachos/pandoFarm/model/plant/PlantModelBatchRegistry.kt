package com.mapachos.pandoFarm.model.plant

import com.mapachos.pandoFarm.model.plant.types.TomatoModelBatch
import org.bukkit.entity.Entity

object PlantModelBatchRegistry {

    init {
        loadDefaultBatches()
    }

    fun loadDefaultBatches(){
        register(TomatoModelBatch)
    }

    val list = mutableListOf<PlantModelBatch<out Entity>>()

    fun register(plantModelBatch: PlantModelBatch<out Entity>) {
        list.add(plantModelBatch)
    }

    fun getById(id: String): PlantModelBatch<out Entity>? {
        return list.find { it.id == id }
    }

    fun getByEntityClass(entityClass: Class<out Entity>): PlantModelBatch<out Entity>? {
        return list.find { it.entityClass == entityClass }
    }
}