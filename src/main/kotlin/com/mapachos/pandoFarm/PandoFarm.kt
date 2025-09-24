package com.mapachos.pandoFarm

import com.mapachos.pandoFarm.plants.engine.management.PlantEventListener
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class PandoFarm : JavaPlugin() {

    override fun onEnable() {
        registerListener(PlantEventListener)
    }

    fun registerListener(listener: Listener){
        server.pluginManager.registerEvents(listener,this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    companion object {
        lateinit var instance: PandoFarm
            private set
    }
}
