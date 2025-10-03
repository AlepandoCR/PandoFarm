package com.mapachos.pandoFarm.model.plant

import com.mapachos.pandoFarm.model.preset.ModelPreset
import com.mapachos.pandoFarm.plants.data.ModelBatchDto
import com.mapachos.pandoFarm.plants.engine.GrowthStage
import org.bukkit.Location
import org.bukkit.entity.Entity

class PlantModelBatch<E: Entity>(val id: String, val entityClass: Class<E>) {

     fun seedlingModel(location: Location): ModelPreset<E>{
        return object: ModelPreset<E>{
            override fun classT(): Class<E> {
                return entityClass
            }

            override fun modelId(): String {
                return "${id}_seedling"
            }

        }
    }

    fun youngModel(location: Location): ModelPreset<E>{
        return object: ModelPreset<E>{
            override fun classT(): Class<E> {
                return entityClass
            }

            override fun modelId(): String {
                return "${id}_young"
            }

        }
    }

    fun vegetativeModel(location: Location): ModelPreset<E>{
        return object: ModelPreset<E>{
            override fun classT(): Class<E> {
                return entityClass
            }

            override fun modelId(): String {
                return "${id}_vegetative"
            }

        }
    }

    fun matureModel(location: Location): ModelPreset<E>{
        return object: ModelPreset<E>{
            override fun classT(): Class<E> {
                return entityClass
            }

            override fun modelId(): String {
                return "${id}_mature"
            }

        }
    }

    fun getModelForStage(stage: GrowthStage, location: Location): ModelPreset<E> {
        return when(stage) {
            GrowthStage.SEEDLING -> seedlingModel(location)
            GrowthStage.YOUNG -> youngModel(location)
            GrowthStage.VEGETATIVE -> vegetativeModel(location)
            GrowthStage.MATURE -> matureModel(location)
        }
    }

    fun toDto(): ModelBatchDto{
        return ModelBatchDto(id, entityClass.simpleName)
    }
}