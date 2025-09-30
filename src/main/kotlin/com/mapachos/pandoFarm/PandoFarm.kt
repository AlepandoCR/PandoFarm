package com.mapachos.pandoFarm

import com.mapachos.pandoFarm.database.MySQLManager
import com.mapachos.pandoFarm.database.table.types.FarmSalesTable
import com.mapachos.pandoFarm.database.table.types.HarvestPlantTable
import com.mapachos.pandoFarm.database.table.types.PlayerDataTable
import com.mapachos.pandoFarm.database.table.types.StaticPlantTable
import com.mapachos.pandoFarm.market.engine.FarmMarketManager
import com.mapachos.pandoFarm.plants.PlantType
import com.mapachos.pandoFarm.plants.engine.Plant
import com.mapachos.pandoFarm.plants.engine.PlantTypeRegistry
import com.mapachos.pandoFarm.plants.engine.harvest.HarvestTypeRegistry
import com.mapachos.pandoFarm.plants.engine.management.GlobalPlantRegistry
import com.mapachos.pandoFarm.plants.engine.management.PlantEventListener
import com.mapachos.pandoFarm.plants.engine.seeds.listener.SeedListener
import com.mapachos.pandoFarm.player.management.PlayerDataManager
import org.bukkit.command.CommandExecutor
import org.bukkit.entity.Entity
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class PandoFarm : JavaPlugin() {

    private lateinit var mysql: MySQLManager

    private lateinit var pStaticPlantsTable: StaticPlantTable

    private lateinit var pHarvestPlantTable: HarvestPlantTable

    private lateinit var pPlayerDataTable: PlayerDataTable

    private lateinit var pFarmSalesTable: FarmSalesTable

    private lateinit var pGlobalPlantRegistry: GlobalPlantRegistry

    override fun onEnable() {
        pInstance = this
        mySql()

        manager()

        startRegistries()

        listeners()
    }

    private fun listeners() {
        registerListener(SeedListener(this))
        registerListener(PlantEventListener(this))
    }

    private fun manager() {
        PlayerDataManager.start(this)
        FarmMarketManager.loadAllMarkets(this)
    }

    private fun startRegistries() {
        pGlobalPlantRegistry = GlobalPlantRegistry(this)
        HarvestTypeRegistry.start()
        PlantTypeRegistry.start()
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
        pFarmSalesTable = FarmSalesTable(mysql)
        pFarmSalesTable.createTable()
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

    fun getFarmSalesTable(): FarmSalesTable {
        if(!this::pFarmSalesTable.isInitialized){
            throw IllegalStateException("FarmSalesTable is not initialized")
        }
        return pFarmSalesTable
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
