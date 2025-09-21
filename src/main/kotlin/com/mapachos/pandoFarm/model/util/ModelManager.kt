package model.util

import model.Model
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity

object ModelManager {
    val repo = mutableListOf<Model<out Entity>>()

    fun register(vararg model: Model<out Entity>){
        repo.addAll(model)
    }

    inline fun <reified T : Entity> modelOf(name: String, world: World, location: Location): Model<T> {
        return Model(name, world, location, T::class.java)
    }

    fun <T: Entity> Class<T>.buildModel(name: String, world: World, location: Location): Model<T> {
        return Model(name, world, location, this)
    }

    fun Entity.getModel(): Model<out Entity>?{
        repo.forEach {
            if(it.entity == this){
                return it
            }
        }

        return null
    }

    fun unregister(vararg model: Model<out Entity>){
        repo.removeAll(model.toSet())
    }
}