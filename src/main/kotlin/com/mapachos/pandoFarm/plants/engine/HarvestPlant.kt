package com.mapachos.pandoFarm.plants.engine

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.database.data.LocationDto.Companion.toDto
import com.mapachos.pandoFarm.database.data.TimeStampDto
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.data.HarvestPlantDto
import com.mapachos.pandoFarm.plants.data.PlantDto
import com.mapachos.pandoFarm.plants.engine.event.plant.HarvestPlantEvent
import com.mapachos.pandoFarm.plants.engine.harvest.Harvest
import com.mapachos.pandoFarm.util.config.ConfigPath
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.time.Duration
import java.util.*

class HarvestPlant<E: Entity>(
    location: Location,
    plantType: PlantType<E>,
    val harvest: Harvest,
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
        return HarvestPlantDto(uniqueIdentifier.toString(), plantType.toDto(), age, location.toDto(), TimeStampDto.now(), harvest.toDto())
    }

    override fun harvest(player: Player) {
        HarvestPlantEvent(player, this, harvest).callEvent()
    }

    override fun save(plugin: PandoFarm) {
        val dto = toDto() as HarvestPlantDto
        plugin.getHarvestPlantTable().insertOrUpdate(dto)
    }

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
            // Update age based on offline elapsed time and clamp to matureAge
            val newAge = calculateNewAge(plant.age, dto.timeStamp)
            plant.age = plant.matureAge.coerceAtMost(newAge)
            return plant
        }

        private fun calculateNewAge(currentAge: Long, timeStamp: TimeStampDto): Long {
            val nowLdt = TimeStampDto.now().toLocalDateTime()
            val thenLdt = timeStamp.toLocalDateTime()

            val elapsedMillis = run {
                val d = Duration.between(thenLdt, nowLdt)
                val ms = d.toMillis()
                if (ms < 0) 0L else ms
            }

            // Translate real time to logical age increments based on config
            val plugin = PandoFarm.getInstance()
            val taskPeriodTicks = plugin.config.getLong(ConfigPath.GROWTH_TASK_PERIOD_TICKS.path).takeIf { it > 0 } ?: 20L
            val ageIncrement = plugin.config.getLong(ConfigPath.GROWTH_AGE_INCREMENT.path).takeIf { it > 0 } ?: 1L
            val periodMillis = taskPeriodTicks * 50L
            if (periodMillis <= 0) return currentAge

            val increments = elapsedMillis / periodMillis
            val delta = increments * ageIncrement

            val sum = currentAge + delta
            return if (sum < 0) 0 else sum
        }
    }
}