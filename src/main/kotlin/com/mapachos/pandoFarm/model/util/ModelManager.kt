package com.mapachos.pandoFarm.model.util

import com.mapachos.pandoFarm.model.Model
import org.bukkit.Location
import org.bukkit.entity.Entity

object ModelManager {
    val repo = mutableListOf<Model<out Entity>>()

    fun register(vararg model: Model<out Entity>){
        repo.addAll(model)
    }

    inline fun <reified T : Entity> modelOf(name: String, location: Location): Model<T> {
        return Model(name, location, T::class.java)
    }

    fun <T: Entity> Class<T>.buildModel(name: String, location: Location): Model<T> {
        return Model(name, location, this)
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