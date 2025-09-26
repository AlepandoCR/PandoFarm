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
    uniqueIdentifier: UUID = UUID.randomUUID()
) : Plant<E>(
    location,
    plantType,
    age,
    uniqueIdentifier
    ) {
    override fun toDto(): PlantDto {
        return StaticPlantDto(uniqueIdentifier, plantType.toDto(), age, location.toDto())
    }

    override fun harvest() {
        // Static plants cannot be harvested
    }

    override fun matureAge(): Long { TODO("Not yet implemented") }

    override fun task() { TODO("Not yet implemented") }

    override fun save() {
        val dto = toDto() as StaticPlantDto
        PandoFarm.getInstance().getStaticPlantTable().insertOrUpdate(dto)
    }

    override fun interact() { TODO("Not yet implemented") }

    companion object{

        fun load(dto: StaticPlantDto): StaticPlant<out Entity> {
            val type = dto.plantType.toPlantType()
            return StaticPlant(
                dto.location.toLocation(),
                type,
                dto.age,
                dto.uniqueIdentifier
            )
        }
    }
}