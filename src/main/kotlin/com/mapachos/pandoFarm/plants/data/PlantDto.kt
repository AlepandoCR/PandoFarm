package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.Dto
import com.mapachos.pandoFarm.database.data.LocationDto
import java.util.*

abstract class PlantDto(
    val uniqueIdentifier: UUID,
    val plantType: String,
    val age: Long,
    val location: LocationDto
): Dto {
}