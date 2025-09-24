package com.mapachos.pandoFarm.plants.engine

import com.mapachos.pandoFarm.database.data.LocationDto.Companion.toDto
import com.mapachos.pandoFarm.model.plant.PlantModelBatch
import com.mapachos.pandoFarm.model.preset.ModelPreset
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.data.HarvestPlantDto
import com.mapachos.pandoFarm.plants.data.PlantDto
import com.mapachos.pandoFarm.plants.engine.harvest.Harvest
import com.mapachos.pandoFarm.plants.engine.harvest.HarvestType
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Entity
import java.util.*

class HarvestPlant<E: Entity,HT: HarvestType>(
    location: Location,
    plantType: PlantType,
    modelBatch: PlantModelBatch<E>,
    val harvest: Harvest<HT>,
    age: Long = 0,
    uniqueIdentifier: UUID = UUID.randomUUID()
) : Plant<E>(
    location,
    plantType,
    modelBatch,
    age,
    uniqueIdentifier
    ) {
    override fun toDto(): PlantDto {
        return HarvestPlantDto(uniqueIdentifier, plantType.name, age, location.toDto(), getModelBatchID(), harvest.toDto())
    }

    override fun onSpawn() {
        TODO("Not yet implemented")
    }

    override fun grow() {
        TODO("Not yet implemented")
    }

    override fun harvest() {
        TODO("Not yet implemented")
    }

    override fun matureAge(): Long {
        TODO("Not yet implemented")
    }

    override fun task() {
        TODO("Not yet implemented")
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }

    override fun save() {
        TODO("Not yet implemented")
    }

    override fun load(dto: PlantDto) {
        TODO("Not yet implemented")
    }

    override fun interact() {
        TODO("Not yet implemented")
    }

}