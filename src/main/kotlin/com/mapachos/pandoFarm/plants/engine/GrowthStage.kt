package com.mapachos.pandoFarm.plants.engine

import org.bukkit.entity.Entity

enum class GrowthStage {
    SEEDLING,
    YOUNG,
    VEGETATIVE,
    MATURE;

    fun hasNext(): Boolean {
        return this != MATURE
    }

    fun next(): GrowthStage {
        return when (this) {
            SEEDLING -> YOUNG
            YOUNG -> VEGETATIVE
            VEGETATIVE -> MATURE
            MATURE -> MATURE
        }
    }

    companion object {
        fun fromPlant(plant: Plant<out Entity>): GrowthStage {
            val age = plant.age
            val maxAge = plant.matureAge()
            return fromRange(age, maxAge)
        }

        fun fromRange(age: Long, maxAge: Long): GrowthStage {
            val stageDuration = maxAge / 4
            return when {
                age < stageDuration -> SEEDLING
                age < stageDuration * 2 -> YOUNG
                age < stageDuration * 3 -> VEGETATIVE
                else -> MATURE
            }
        }

    }
}