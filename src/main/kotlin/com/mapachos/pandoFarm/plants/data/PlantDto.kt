package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.ContainerDto
import com.mapachos.pandoFarm.database.data.LocationDto
import com.mapachos.pandoFarm.database.data.TimeStampDto

abstract class PlantDto(
    val uniqueIdentifier: String,
    val plantType: PlantTypeDto,
    val age: Long,
    val location: LocationDto,
    val timeStamp: TimeStampDto
): ContainerDto