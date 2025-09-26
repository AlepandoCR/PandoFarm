package com.mapachos.pandoFarm.plants.engine.harvest.effect

import com.mapachos.pandoFarm.plants.data.HarvestEffectDto
import com.mapachos.pandoFarm.plants.engine.harvest.effect.types.NoneHarvestEffect

class HarvestEffectType(val harvestEffect: HarvestEffect) {

    fun toDto(): HarvestEffectDto{
        return HarvestEffectDto(harvestEffect.name, harvestEffect.description())
    }
}