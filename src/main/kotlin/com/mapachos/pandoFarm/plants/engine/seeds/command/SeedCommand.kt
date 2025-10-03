package com.mapachos.pandoFarm.plants.engine.seeds.command

import com.mapachos.pandoFarm.plants.engine.PlantTypeRegistry
import com.mapachos.pandoFarm.plants.engine.seeds.Seed
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class SeedCommand: CommandExecutor, TabCompleter {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player){
            sender.sendMessage("Only players can use this command.")
            return true
        }
        if(args.isEmpty()){
            sender.sendMessage("Usage: /$label <plantTypeName>")
            return true
        }
        val nameArg = args[0]
        val plantType = PlantTypeRegistry.getPlantTypeByName(nameArg)
        if(plantType == null){
            sender.sendMessage("Unknown plant type: $nameArg")
            return true
        }
        val dto = plantType.toDto()
        // Material por defecto para las semillas
        val item = Seed(Material.KNOWLEDGE_BOOK, dto).buildItem()
        val leftover = sender.inventory.addItem(item)
        if(leftover.isNotEmpty()){
            leftover.values.forEach { sender.world.dropItemNaturally(sender.location, it) }
            sender.sendMessage("Inventory full, dropped seed at your feet.")
        } else {
            sender.sendMessage("Seed given: ${plantType.name}")
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        if(args.size == 1){
            val partial = args[0].lowercase()
            return PlantTypeRegistry.plants.map { it.name }
                .filter { it.lowercase().startsWith(partial) }
                .sorted()
                .toMutableList()
        }
        return mutableListOf()
    }
}