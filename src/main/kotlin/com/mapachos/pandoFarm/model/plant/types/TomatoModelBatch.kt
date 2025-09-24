package com.mapachos.pandoFarm.model.plant.types

import com.mapachos.pandoFarm.model.plant.PlantModelBatch
import org.bukkit.entity.ArmorStand

object TomatoModelBatch: PlantModelBatch<ArmorStand>() {
    override val id: String
        get() = "tomato"
    override val entityClass: Class<ArmorStand>
        get() = ArmorStand::class.java
}
