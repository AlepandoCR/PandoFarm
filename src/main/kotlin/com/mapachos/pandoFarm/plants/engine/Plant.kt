package com.mapachos.pandoFarm.plants.engine

import com.mapachos.pandoFarm.model.Model
import com.mapachos.pandoFarm.model.preset.ModelPreset
import com.mapachos.pandoFarm.util.DynamicListener
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*
import kotlin.reflect.KClass

abstract class Plant<H: InteractionMethod, E: Entity, I: InteractionMethod>(
    val location: Location,
    val age: Long,
    val uniqueIdentifier: UUID,
    val modelPreset: ModelPreset<E>,
    val harvestMethod: H,
    val interactionMethod: I,
) {
    val dynamicListener = DynamicListener()

    lateinit var model: Model<E>

    init {
        dynamicListener.setListener(listener(harvestMethod.eventClass, interactionMethod.eventClass))
    }

    fun spawn(location: Location) {
        onSpawn()
        model = modelPreset.buildModel(location)
    }

    abstract fun onSpawn()

    abstract fun grow()

    abstract fun growInterval(): Double

    abstract fun tick()

    abstract fun harvest()

    abstract fun isMature(): Boolean

    abstract fun pause()

    abstract fun resume()

    abstract fun destroy()

    abstract fun save()

    abstract fun load()

    abstract fun interact()

    fun <C: KClass<out Event>, Z: KClass<out Event>>listener(harvestEventClass: C, interactEventClass: Z): Listener {
        val listener =  object : Listener {
            @EventHandler
            fun onInteract(event: Event){
                if(harvestEventClass.isInstance(event)){
                    harvest()
                }

                if(interactEventClass.isInstance(event)){
                    interact()
                }
            }
        }
        return listener
    }
}