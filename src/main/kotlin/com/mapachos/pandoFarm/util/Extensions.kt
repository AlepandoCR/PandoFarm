package gg.cloudworld.map.util

import gg.cloudworld.map.MAP
import gg.cloudworld.map.analytics.AnalysisRequest
import gg.cloudworld.map.analytics.util.PlayerFilterLogic
import gg.cloudworld.map.data.PlayerDto
import gg.cloudworld.map.data.SaleDto
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Transformation
import org.joml.Quaternionf
import java.util.UUID

private val plugin: JavaPlugin = JavaPlugin.getPlugin(MAP::class.java)

fun async(handler: () -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, handler)
}


fun sync(handler: () -> Unit) {
    Bukkit.getScheduler().runTask(plugin, handler)
}


fun later(handler: () -> Unit, delay: Long) {
    Bukkit.getScheduler().runTaskLater(plugin, handler,delay)
}

fun timer(handler: () -> Unit, delay: Long, period: Long): BukkitTask {
    val runnable = Bukkit.getScheduler().runTaskTimer(plugin, handler,delay,period)
    return runnable
}

fun BukkitRunnable.timer(delay: Long,period: Long){
    this.runTaskTimer(plugin,delay,period)
}

fun timerRunnable(handler: () -> Unit): BukkitRunnable {
    val runnable = object : BukkitRunnable(){
        override fun run(){
            handler.invoke()
        }
    }
    return runnable
}

fun distance(p1: Location, p2: Location): Double{
    return p1.distance(p2)
}

fun Entity.hide(player: Player){
    player.hideEntity(plugin,this)
}

fun Entity.hideAllExcept(player: Player){
    Bukkit.getOnlinePlayers().forEach {
        if(it != player) this.hide(it)
    }
}

fun PersistentDataContainer.entriesAsString(): List<Pair<NamespacedKey, String>> {
    val result = mutableListOf<Pair<NamespacedKey, String>>()

    this.keys.forEach { key ->
        val value = when {
            this.has(key, PersistentDataType.STRING) -> this.get(key, PersistentDataType.STRING)?.toString()
            this.has(key, PersistentDataType.INTEGER) -> this.get(key, PersistentDataType.INTEGER)?.toString()
            this.has(key, PersistentDataType.DOUBLE) -> this.get(key, PersistentDataType.DOUBLE)?.toString()
            this.has(key, PersistentDataType.LONG) -> this.get(key, PersistentDataType.LONG)?.toString()
            this.has(key, PersistentDataType.BYTE) -> this.get(key, PersistentDataType.BYTE)?.toString()
            this.has(key, PersistentDataType.FLOAT) -> this.get(key, PersistentDataType.FLOAT)?.toString()
            this.has(key, PersistentDataType.SHORT) -> this.get(key, PersistentDataType.SHORT)?.toString()
            this.has(key, PersistentDataType.BYTE_ARRAY) -> this.get(key, PersistentDataType.BYTE_ARRAY)?.joinToString(",") { it.toString() }
            this.has(key, PersistentDataType.INTEGER_ARRAY) -> this.get(key, PersistentDataType.INTEGER_ARRAY)?.joinToString(",") { it.toString() }
            else -> null
        }

        if (value != null) {
            result.add(key to value)
        }
    }

    return result
}

fun Color.toNamedTextColor(): NamedTextColor {
    val adventureTextColor: TextColor = TextColor.color(this.asRGB())

    return NamedTextColor.nearestTo(adventureTextColor)
}

fun List<SaleDto>.filterByTime(request: AnalysisRequest): List<SaleDto> {
    return this.filter { sale ->
        (request.startTime == null || sale.time.toZonedDateTime() >= request.startTime.toZonedDateTime()) &&
                (request.endTime == null || sale.time.toZonedDateTime() <= request.endTime.toZonedDateTime())
    }
}

fun List<SaleDto>.filterByPlayer(request: AnalysisRequest, logic: PlayerFilterLogic): List<SaleDto> {
    if (request.players.isEmpty()) return this
    return this.filter { sale ->
        when (logic) {
            PlayerFilterLogic.ANY -> sale.seller in request.players || sale.buyer in request.players
            PlayerFilterLogic.SELLER_ONLY -> sale.seller in request.players
            PlayerFilterLogic.BUYER_ONLY -> sale.buyer in request.players
        }
    }
}

fun UUID.toPlayerDto(): PlayerDto = PlayerDto(this)

