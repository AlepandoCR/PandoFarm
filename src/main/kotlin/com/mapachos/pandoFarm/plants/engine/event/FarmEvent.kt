package com.mapachos.pandoFarm.plants.engine.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

abstract class FarmEvent : Event() {
    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlerList
    }
}