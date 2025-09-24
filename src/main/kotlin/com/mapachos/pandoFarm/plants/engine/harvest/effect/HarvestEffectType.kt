package com.mapachos.pandoFarm.plants.engine.harvest.effect

import com.mapachos.pandoFarm.plants.data.HarvestEffectDto
import com.mapachos.pandoFarm.plants.engine.harvest.effect.types.NoneHarvestEffect

enum class HarvestEffectType(val harvestEffect: HarvestEffect) {
    NONE(NoneHarvestEffect),;

    fun toDto(): HarvestEffectDto{
        return HarvestEffectDto(harvestEffect.description())
    }
}