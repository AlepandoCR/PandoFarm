package com.mapachos.pandoFarm.util


import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.player.data.PlayerDto
import com.mapachos.pandoFarm.player.management.PlayerDataManager
import com.mapachos.pandoFarm.util.command.AutoCommand
import com.mapachos.pandoFarm.util.yml.AutoYML
import com.mapachos.pandoFarm.util.yml.DataFolder
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.io.Serializable

private val plugin = PandoFarm.getInstance()

fun Player.farmData(): PlayerDto{
    return PlayerDataManager.getPlayerData(this)
}

fun autoCommand(
    handler: (CommandSender, Command, String, Array<String>) -> Boolean,
    completions: List<String> = emptyList(),
    onlyOp: Boolean = false
): AutoCommand = AutoCommand(handler, completions, onlyOp)


inline fun <reified T: Serializable> autoYml(name: String, dataFolder: DataFolder,header: String? = null): AutoYML<T> =
    AutoYML.create(T::class, name, dataFolder, header)

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


