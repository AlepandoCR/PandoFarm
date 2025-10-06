package com.mapachos.pandoFarm.player.culling.plant

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.util.config.ConfigPath
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * Manages one PlayerLookEngine per online player.
 */
object PlayerPlantLookManager {
    private lateinit var plugin: PandoFarm
    private val engines: MutableMap<UUID, PlayerPlantLookEngine> = mutableMapOf()

    private var scanRadius: Double = 48.0
    private var periodTicks: Long = 5L

    fun start(plugin: PandoFarm) {
        this.plugin = plugin
        // load config
        val cfg = plugin.config
        scanRadius = cfg.getDouble(ConfigPath.LOOK_SCAN_RADIUS.path, 48.0)
        periodTicks = cfg.getLong(ConfigPath.LOOK_PERIOD_TICKS.path, 5L)
        // Start engines for already-online players (e.g., after /reload)
        Bukkit.getOnlinePlayers().forEach { startFor(it) }
    }

    fun stop() {
        engines.values.forEach { it.stop() }
        engines.clear()
    }

    fun startFor(player: Player) {
        val id = player.uniqueId
        if (engines.containsKey(id)) return
        val engine = PlayerPlantLookEngine(plugin, player, scanRadius = scanRadius, periodTicks = periodTicks)
        engines[id] = engine
        engine.start()
    }

    fun stopFor(player: Player) {
        engines.remove(player.uniqueId)?.stop()
    }

    fun restartFor(player: Player) {
        stopFor(player)
        startFor(player)
    }
}
