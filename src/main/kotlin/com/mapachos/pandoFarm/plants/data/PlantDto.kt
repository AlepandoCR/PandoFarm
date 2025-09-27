package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.ContainerDto
import com.mapachos.pandoFarm.database.data.LocationDto

abstract class PlantDto(
    val uniqueIdentifier: String,
    val plantType: PlantTypeDto,
    val age: Long,
    val location: LocationDto,
): ContainerDto