package com.mapachos.pandoFarm.economy

import com.mapachos.pandoFarm.PandoFarm
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*


object EconomyController {
    private var economy: Economy? = null

    fun start(plugin: PandoFarm): Boolean {
        val registration = plugin.server.servicesManager.getRegistration(Economy::class.java)
        if (registration == null) {
            plugin.logger.warning("Economy provider (Vault) not found! Disabling economy features.")
            economy = null
            return false
        }
        economy = registration.provider
        return true
    }

    fun isReady(): Boolean = economy != null

    private fun player(uuid: UUID): OfflinePlayer = Bukkit.getOfflinePlayer(uuid)

    fun format(amount: Double): String = economy?.format(amount) ?: String.format("$%.2f", amount)

    fun getBalance(player: OfflinePlayer): Double = economy?.getBalance(player) ?: 0.0
    fun getBalance(player: Player): Double = getBalance(player as OfflinePlayer)
    fun getBalance(uuid: UUID): Double = getBalance(player(uuid))

    fun has(player: OfflinePlayer, amount: Double): Boolean = economy?.has(player, amount) ?: false
    fun has(player: Player, amount: Double): Boolean = has(player as OfflinePlayer, amount)
    fun has(uuid: UUID, amount: Double): Boolean = has(player(uuid), amount)

    fun deposit(player: OfflinePlayer, amount: Double): Boolean {
        if (amount <= 0.0) return true
        val eco = economy ?: return false
        return eco.depositPlayer(player, amount).transactionSuccess()
    }
    fun deposit(player: Player, amount: Double): Boolean = deposit(player as OfflinePlayer, amount)
    fun deposit(uuid: UUID, amount: Double): Boolean = deposit(player(uuid), amount)

    fun withdraw(player: OfflinePlayer, amount: Double): Boolean {
        if (amount <= 0.0) return true
        val eco = economy ?: return false
        return eco.withdrawPlayer(player, amount).transactionSuccess()
    }
    fun withdraw(player: Player, amount: Double): Boolean = withdraw(player as OfflinePlayer, amount)
    fun withdraw(uuid: UUID, amount: Double): Boolean = withdraw(player(uuid), amount)

    fun setBalance(player: OfflinePlayer, newBalance: Double): Boolean {
        val eco = economy ?: return false
        val current = eco.getBalance(player)
        val diff = newBalance - current
        return if (diff > 0) deposit(player, diff) else withdraw(player, -diff)
    }
    fun setBalance(player: Player, newBalance: Double): Boolean = setBalance(player as OfflinePlayer, newBalance)
    fun setBalance(uuid: UUID, newBalance: Double): Boolean = setBalance(player(uuid), newBalance)
}

