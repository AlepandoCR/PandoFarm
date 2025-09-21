package com.mapachos.pandoFarm.plants.engine

import com.mapachos.pandoFarm.model.Model
import org.bukkit.Location
import org.bukkit.entity.Entity
import java.util.UUID

abstract class FruitPlant(
    location: Location,
    age: Long,
    uniqueIdentifier: UUID,
    model: Model<out Entity>
) : Plant(
    location,
    age,
    uniqueIdentifier,
    model
) {

}