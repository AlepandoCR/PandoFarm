package com.mapachos.pandoFarm.plants.engine

import com.mapachos.pandoFarm.model.preset.ModelPreset
import com.mapachos.pandoFarm.plants.engine.harvest.Harvest
import com.mapachos.pandoFarm.plants.engine.harvest.HarvestType
import com.mapachos.pandoFarm.plants.engine.harvest.effect.HarvestEffectType
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Entity
import java.util.UUID

abstract class HarvestPlant<HI: InteractionMethod, E: Entity, I: InteractionMethod,HT: HarvestType, H: Harvest<HT, out HarvestEffectType, out Material>>(
    location: Location,
    age: Long,
    uniqueIdentifier: UUID,
    modelPreset: ModelPreset<E>,
    harvestMethod: HI,
    interactionMethod: I,
    val harvest: H
) : Plant<HI, E, I>(
    location,
    age,
    uniqueIdentifier,
    modelPreset,
    harvestMethod,
    interactionMethod,
    ) {

}