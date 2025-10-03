package com.mapachos.pandoFarm.plants.engine.harvest.effect

import com.mapachos.pandoFarm.plants.engine.harvest.effect.types.NoneHarvestEffect

object HarvestEffectRegistry {
    val registry = mutableListOf<HarvestEffect>()

    fun start() {
        loadDefaultEffects()
    }

    fun loadDefaultEffects() {
        register(NoneHarvestEffect)
    }

    fun register(harvestEffect: HarvestEffect) {
        registry.add(harvestEffect)
    }

    fun getByName(name: String): HarvestEffect {
        val found = registry.find { it.name == name } ?: throw IllegalArgumentException("No HarvestEffect found with name: $name")
        return found
    }
}