package com.mapachos.pandoFarm.plants.engine

import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import kotlin.reflect.KClass

enum class InteractionMethod(val eventClass: KClass<out Event>) {
    INTERACT(PlayerInteractAtEntityEvent::class),
    DAMAGE(EntityDamageByEntityEvent::class),
}