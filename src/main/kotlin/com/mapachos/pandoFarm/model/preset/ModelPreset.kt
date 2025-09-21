package model.preset

import model.Model
import model.util.ModelManager.buildModel
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity

interface ModelPreset<T: Entity> {
    fun buildModel(world: World, location: Location): Model<T>{
        return classT().buildModel(modelId(),world,location)
    }

    fun classT(): Class<T>

    fun modelId(): String
}