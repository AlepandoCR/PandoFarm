package com.mapachos.pandoFarm.model.preset

import com.mapachos.pandoFarm.model.Model
import com.mapachos.pandoFarm.model.util.ModelManager.buildModel
import org.bukkit.Location
import org.bukkit.entity.Entity

interface ModelPreset<E: Entity> {
    fun buildModel(location: Location): Model<E>{
        val world = location.world
        return classT().buildModel(modelId(),world,location)
    }

    fun classT(): Class<E>

    fun modelId(): String
}