package com.mapachos.pandoFarm.plants.engine

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.database.data.LocationDto.Companion.toDto
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.data.HarvestPlantDto
import com.mapachos.pandoFarm.plants.data.PlantDto
import com.mapachos.pandoFarm.plants.engine.harvest.Harvest
import org.bukkit.Location
import org.bukkit.entity.Entity
import java.util.*

class HarvestPlant<E: Entity>(
    location: Location,
    plantType: PlantType<E>,
    val plantHarvest: Harvest,
    age: Long = 0,
    uniqueIdentifier: UUID = UUID.randomUUID(),
    matureAge: Long
) : Plant<E>(
    location,
    plantType,
    age,
    uniqueIdentifier,
    matureAge
    ) {
    override fun toDto(): PlantDto {
        return HarvestPlantDto(uniqueIdentifier.toString(), plantType.toDto(), age, location.toDto(), plantHarvest.toDto())
    }

    override fun harvest() { TODO("Not yet implemented") }

    override fun save(plugin: PandoFarm) {
        val dto = toDto() as HarvestPlantDto
        plugin.getHarvestPlantTable().insertOrUpdate(dto)
    }

    override fun interact() { TODO("Not yet implemented") }

    companion object {

        fun load(dto: HarvestPlantDto): HarvestPlant<out Entity> {
            val type = dto.plantType.toPlantType()
            val harvest = dto.harvestDto.toHarvest()
            val location = dto.location.toLocation()
            val plant =  HarvestPlant(
                location,
                type,
                harvest,
                dto.age,
                UUID.fromString(dto.uniqueIdentifier),
                type.matureAge
            )
            return plant
        }
    }
}