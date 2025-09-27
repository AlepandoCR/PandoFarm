package com.mapachos.pandoFarm

import com.mapachos.pandoFarm.database.MySQLManager
import com.mapachos.pandoFarm.database.table.types.HarvestPlantTable
import com.mapachos.pandoFarm.database.table.types.PlayerDataTable
import com.mapachos.pandoFarm.database.table.types.StaticPlantTable
import com.mapachos.pandoFarm.plants.engine.PlantTypeRegistry
import com.mapachos.pandoFarm.plants.engine.harvest.HarvestTypeRegistry
import com.mapachos.pandoFarm.plants.engine.management.GlobalPlantRegistry
import com.mapachos.pandoFarm.plants.engine.management.PlantEventListener
import com.mapachos.pandoFarm.player.management.PlayerDataManager
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class PandoFarm : JavaPlugin() {

    private lateinit var mysql: MySQLManager

    private lateinit var pStaticPlantsTable: StaticPlantTable

    private lateinit var pHarvestPlantTable: HarvestPlantTable

    private lateinit var pPlayerDataTable: PlayerDataTable

    private lateinit var pGlobalPlantRegistry: GlobalPlantRegistry

    override fun onEnable() {
        pInstance = this
        mySql()

        PlayerDataManager.start(this)
        pGlobalPlantRegistry = GlobalPlantRegistry(this)

        HarvestTypeRegistry.start()
        PlantTypeRegistry.start()

        registerListener(PlantEventListener(this))
    }

    override fun onDisable() {
        mysql.disconnect()
        PlayerDataManager.stop()
    }

    private fun mySql() {
        mysql = MySQLManager(this)
        mysql.connect()

        startTables()
    }

    private fun startTables() {
        pStaticPlantsTable = StaticPlantTable(mysql)
        pStaticPlantsTable.createTable()
        pHarvestPlantTable = HarvestPlantTable(mysql)
        pHarvestPlantTable.createTable()
        pPlayerDataTable = PlayerDataTable(mysql)
        pPlayerDataTable.createTable()
    }

    fun registerCommand(name: String, executor: CommandExecutor) {
        getCommand(name)?.setExecutor(executor)
    }

    fun registerListener(listener: Listener){
        server.pluginManager.registerEvents(listener,this)
    }

    fun getGlobalPlantRegistry(): GlobalPlantRegistry {
        if(!this::pGlobalPlantRegistry.isInitialized){
            throw IllegalStateException("GlobalPlantRegistry is not initialized")
        }
        return pGlobalPlantRegistry
    }

    fun getStaticPlantTable(): StaticPlantTable {
        if(!this::pStaticPlantsTable.isInitialized){
            throw IllegalStateException("StaticPlantTable is not initialized")
        }
        return pStaticPlantsTable
    }

    fun getPlayerDataTable(): PlayerDataTable {
        if(!this::pPlayerDataTable.isInitialized){
            throw IllegalStateException("PlayerDataTable is not initialized")
        }
        return pPlayerDataTable
    }

    fun getHarvestPlantTable(): HarvestPlantTable {
        if(!this::pHarvestPlantTable.isInitialized){
            throw IllegalStateException("HarvestPlantTable is not initialized")
        }
        return pHarvestPlantTable
    }

    companion object {
         private lateinit var pInstance: PandoFarm

        fun getInstance(): PandoFarm {
            if(!this::pInstance.isInitialized){
                throw IllegalStateException("PandoFarm is not initialized")
            }
            return pInstance
        }
    }
}
