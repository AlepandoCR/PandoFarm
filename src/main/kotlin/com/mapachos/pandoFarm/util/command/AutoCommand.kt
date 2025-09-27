package com.mapachos.pandoFarm.util.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * AutoCommand allows easy creation of Bukkit commands with tab completion and permission filters.
 * @param handler The function to execute when the command is run.
 * @param completions The list of strings to use for tab completion.
 * @param onlyOp If true, only OPs can execute the command.
 */
class AutoCommand(
    private val handler: (CommandSender, Command, String, Array<String>) -> Boolean,
    private val completions: List<String> = emptyList(),
    private val onlyOp: Boolean = false
) : CommandExecutor, TabCompleter {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        // Only allow OPs if onlyOp is true
        if (onlyOp && (sender !is Player || !sender.isOp)) {
            sender.sendMessage("§cYou do not have permission to use this command. Command name: ${command.name}")
            return true
        }
        return handler(sender, command, label, args)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        // Only allow OPs to see completions if onlyOp is true
        if (onlyOp && (sender !is Player || !sender.isOp)) {
            return emptyList()
        }
        if (completions.isEmpty()) return emptyList()
        val lastArg = args.lastOrNull() ?: ""
        return completions.filter { it.startsWith(lastArg, ignoreCase = true) }
    }
}