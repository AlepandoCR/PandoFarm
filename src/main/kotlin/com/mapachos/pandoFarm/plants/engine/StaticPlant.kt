package com.mapachos.pandoFarm.plants.engine

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.database.data.LocationDto.Companion.toDto
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.data.PlantDto
import com.mapachos.pandoFarm.plants.data.StaticPlantDto
import org.bukkit.Location
import org.bukkit.entity.Entity
import java.util.*

class StaticPlant<E: Entity>(
    location: Location,
    plantType: PlantType<E>,
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
        return StaticPlantDto(uniqueIdentifier.toString(), plantType.toDto(), age, location.toDto())
    }

    override fun save(plugin: PandoFarm) {
        val dto = toDto() as StaticPlantDto
        plugin.getStaticPlantTable().insertOrUpdate(dto)
    }

    override fun interact() { TODO("Not yet implemented") }

    companion object{

        fun load(dto: StaticPlantDto): StaticPlant<out Entity> {
            val type = dto.plantType.toPlantType()
            val location = dto.location.toLocation()
            val plant = StaticPlant(
                location,
                type,
                dto.age,
                UUID.fromString(dto.uniqueIdentifier),
                type.matureAge
            )
            return plant
        }
    }
}