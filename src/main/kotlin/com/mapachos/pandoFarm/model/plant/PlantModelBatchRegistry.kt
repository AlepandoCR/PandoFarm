package com.mapachos.pandoFarm.model.plant

import org.bukkit.entity.Entity

object PlantModelBatchRegistry {

    val list = mutableListOf<PlantModelBatch<out Entity>>()

    fun register(plantModelBatch: PlantModelBatch<out Entity>) {
        list.add(plantModelBatch)
    }

    fun getById(id: String): PlantModelBatch<out Entity>? {
        return list.find { it.id == id }
    }

    fun getTypedById(id: String, entityClass: Class<out Entity>): PlantModelBatch<out Entity>? {
        val found = list.find { it.id == id } ?: return null
        if(found.entityClass != entityClass) return null
        return found
    }


    fun getByEntityClass(entityClass: Class<out Entity>): PlantModelBatch<out Entity>? {
        return list.find { it.entityClass == entityClass }
    }

    fun <E: Entity>serveID(id: String, entityClass: Class<E>): PlantModelBatch<E>{
        val modelBatch = PlantModelBatch(id, entityClass)
        if(!list.contains(modelBatch)){
            register(modelBatch)
        }

        return modelBatch
    }
}