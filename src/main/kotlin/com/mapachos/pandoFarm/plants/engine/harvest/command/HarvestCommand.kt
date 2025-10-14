package com.mapachos.pandoFarm.plants.engine.harvest.command

import com.mapachos.pandoFarm.plants.engine.harvest.HarvestTypeRegistry
import com.mapachos.pandoFarm.util.giveItem
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class HarvestCommand: CommandExecutor, TabCompleter {
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
        val harvestType = HarvestTypeRegistry.getHarvestByName(nameArg)

        if (harvestType == null){
            sender.sendMessage("Unknown harvest type : $harvestType")
            return true
        }
        val item = harvestType.buildHarvest().harvestItem.buildItem()
        sender.giveItem(item)
        sender.sendMessage("Harvest given: ${harvestType.name}")
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
            return HarvestTypeRegistry.harvests
                .map { it.name }
                .filter { it.lowercase().startsWith(partial) }
                .sorted()
                .toMutableList()
        }
        return mutableListOf()
    }
}
