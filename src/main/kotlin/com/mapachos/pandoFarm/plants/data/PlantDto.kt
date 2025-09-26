package com.mapachos.pandoFarm.plants.data

import com.mapachos.pandoFarm.database.data.Dto
import com.mapachos.pandoFarm.database.data.LocationDto
import com.mapachos.pandoFarm.database.data.persistance.DataNamespacedKey
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler
import io.papermc.paper.threadedregions.scheduler.RegionScheduler
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.*

abstract class PlantDto(
    val uniqueIdentifier: UUID,
    val plantType: PlantTypeDto,
    val age: Long,
    val location: LocationDto,
): Dto