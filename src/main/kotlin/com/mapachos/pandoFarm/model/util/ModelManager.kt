package com.mapachos.pandoFarm.model.util

import com.mapachos.pandoFarm.model.Model
import org.bukkit.Location
import org.bukkit.entity.Entity

object ModelManager {
    val models = mutableListOf<Model<out Entity>>()

    fun register(vararg model: Model<out Entity>){
        models.addAll(model)
    }

    inline fun <reified T : Entity> modelOf(name: String, location: Location): Model<T> {
        return Model(name, location, T::class.java)
    }

    fun <T: Entity> Class<T>.buildModel(name: String, location: Location): Model<T> {
        return Model(name, location, this)
    }

    fun Entity.getModel(): Model<out Entity>?{
        models.forEach {
            if(it.entity == this){
                return it
            }
        }

        return null
    }

    fun unregister(vararg model: Model<out Entity>){
        models.removeAll(model.toSet())
    }

    fun clear(){
        val copy = models.toList()
        copy.forEach { it.remove() }
        models.clear()
    }
}