package com.mapachos.pandoFarm.player.management

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.player.data.PlayerDto
import com.mapachos.pandoFarm.util.listeners.DynamicListener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerDataManager {

    lateinit var plugin: PandoFarm

    val dynamicListener = DynamicListener()

    val data = mutableMapOf<Player,PlayerDto>()

    init {
        dynamicListener.setListener(listener())
    }

    fun start(plugin: PandoFarm) {
        this.plugin = plugin
        dynamicListener.start()
    }

    fun stop(){
        dynamicListener.stop()
        data.forEach { (player, _) ->
            saveAndRemovePlayerData(player)
        }
    }

    fun getPlayerData(player: Player): PlayerDto {
        return data[player] ?: loadPlayerData(player)
    }

    private fun loadPlayerData(player: Player): PlayerDto{
        val data = plugin.getPlayerDataTable().findBy("uuid", player.uniqueId.toString()) ?: PlayerDto.create(player.uniqueId.toString())
        this.data[player] = data

        return data
    }

    fun saveAndRemovePlayerData(player: Player){
        data[player]?.let {
            plugin.getPlayerDataTable().insertOrUpdate(it)
            this.data.remove(player)
        }
    }

    private fun listener(): Listener{
        return object : Listener {
            @EventHandler(priority = EventPriority.HIGHEST)
            fun onPlayerJoin(event: PlayerJoinEvent){
                loadPlayerData(event.player)
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            fun onPlayerQuit(event: PlayerQuitEvent){
                saveAndRemovePlayerData(event.player)
            }
        }
    }
}