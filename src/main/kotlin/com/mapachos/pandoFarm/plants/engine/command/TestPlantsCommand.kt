package com.mapachos.pandoFarm.plants.engine.command

import com.mapachos.pandoFarm.PandoFarm
import com.mapachos.pandoFarm.economy.market.ui.MarketMenu
import com.mapachos.pandoFarm.plants.engine.PlantTypeRegistry
import com.mapachos.pandoFarm.plants.engine.StaticPlant
import com.mapachos.pandoFarm.plants.engine.harvest.HarvestTypeRegistry
import com.mapachos.pandoFarm.util.autoCommand
import org.bukkit.Location
import org.bukkit.command.CommandExecutor
import org.bukkit.entity.Player
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sqrt

object TestPlantsCommand {

    fun getCommand(): CommandExecutor = autoCommand(
        handler = { sender, _, _, args ->
            if (sender !is Player) {
                sender.sendMessage("Solo jugadores pueden usar este comando.")
                return@autoCommand true
            }

            val plugin = PandoFarm.getInstance()
            val registry = plugin.getGlobalPlantRegistry()

            if (args.size == 1 && args[0].equals("market", ignoreCase = true)) {
                val harvest = HarvestTypeRegistry.harvests.firstOrNull()
                if (harvest == null) {
                    sender.sendMessage("No hay tipos de cosecha (harvest) cargados.")
                    return@autoCommand true
                }
                val menu = MarketMenu(sender, harvest, plugin)
                sender.showDialog(menu.mainMenuDialog)
                return@autoCommand true
            }

            // Subcomando: eliminar todas las plantas del mundo actual (físicas + DB)
            if (args.size == 1 && args[0].equals("deleteall", ignoreCase = true)) {
                val world = sender.world
                val worldRegistry = registry.getRegistryForWorld(world)
                val loaded = worldRegistry.registry.toList()
                var removed = 0
                loaded.forEach { plant ->
                    plant.remove(plugin) // elimina del mundo y DB
                    removed++
                }
                // Asegurar limpieza de DB para plantas no cargadas
                val staticLeft = plugin.getStaticPlantTable().getAll().filter { it.location.world == world.name }
                staticLeft.forEach { dto -> plugin.getStaticPlantTable().deleteById(dto.uniqueIdentifier) }
                val harvestLeft = plugin.getHarvestPlantTable().getAll().filter { it.location.world == world.name }
                harvestLeft.forEach { dto -> plugin.getHarvestPlantTable().deleteById(dto.uniqueIdentifier) }

                sender.sendMessage("Eliminadas $removed plantas cargadas y limpiadas ${staticLeft.size + harvestLeft.size} entradas de DB en ${world.name}.")
                return@autoCommand true
            }

            val type = PlantTypeRegistry.plants.firstOrNull()
            if (type == null) {
                sender.sendMessage("No hay tipos de plantas cargados.")
                return@autoCommand true
            }

            val raw = args[0]
            val requested = raw.toLongOrNull()
            if (requested == null || requested <= 0) {
                sender.sendMessage("Cantidad inválida: '$raw'.")
                return@autoCommand true
            }

            val MAX_TOTAL = 1_000_000L
            val total: Long
            val capped: Boolean
            if (requested > MAX_TOTAL) {
                total = MAX_TOTAL
                capped = true
            } else {
                total = requested
                capped = false
            }

            if (capped) sender.sendMessage("Cantidad muy alta, se limita a $MAX_TOTAL para evitar lag.")

            val world = sender.world
            val base = sender.location.block.location
            val perLayer = 100 * 100
            val fullLayers = (total / perLayer).toInt()
            val remainder = (total % perLayer).toInt()

            var placed = 0L

            fun placeAt(x: Int, y: Int, z: Int) {
                val loc = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                val plant = StaticPlant(loc, type, age = 0, matureAge = type.matureAge)
                plant.spawn(loc) // registrará en GlobalPlantRegistry vía PlantSpawnEvent
                placed++
            }

            // Capas completas 100x100
            for (layer in 0 until fullLayers) {
                val y = base.blockY + layer
                for (i in 0 until 100) {
                    for (j in 0 until 100) {
                        placeAt(base.blockX + i, y, base.blockZ + j)
                    }
                }
            }

            // Resto: cuadrado mínimo posible hasta 100x100
            if (remainder > 0) {
                val side = min(100, ceil(sqrt(remainder.toDouble())).toInt())
                val y = base.blockY + fullLayers
                var left = remainder
                loop@ for (i in 0 until side) {
                    for (j in 0 until side) {
                        placeAt(base.blockX + i, y, base.blockZ + j)
                        left--
                        if (left <= 0) break@loop
                    }
                }
            }

            sender.sendMessage("Generadas $placed plantas de tipo ${type.name} en ${fullLayers + if (remainder > 0) 1 else 0} capa(s).")
            true
        },
        completions = listOf("deleteall", "market"),
        onlyOp = true,
        minArgs = 1,
        maxArgs = 1,
        usage = "/testplants <cantidad|deleteall|market>"
    )
}
