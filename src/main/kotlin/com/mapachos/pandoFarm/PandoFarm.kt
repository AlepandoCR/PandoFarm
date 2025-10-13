package com.mapachos.pandoFarm

import com.mapachos.pandoFarm.database.MySQLManager
import com.mapachos.pandoFarm.database.table.types.FarmSalesTable
import com.mapachos.pandoFarm.database.table.types.HarvestPlantTable
import com.mapachos.pandoFarm.database.table.types.PlayerDataTable
import com.mapachos.pandoFarm.database.table.types.StaticPlantTable
import com.mapachos.pandoFarm.economy.market.engine.FarmMarketManager
import com.mapachos.pandoFarm.model.util.ModelManager
import com.mapachos.pandoFarm.plants.engine.PlantTypeRegistry
import com.mapachos.pandoFarm.plants.engine.command.TestPlantsCommand
import com.mapachos.pandoFarm.plants.engine.harvest.HarvestTypeRegistry
import com.mapachos.pandoFarm.plants.engine.harvest.effect.HarvestEffectRegistry
import com.mapachos.pandoFarm.plants.engine.management.GlobalPlantRegistry
import com.mapachos.pandoFarm.plants.engine.management.PlantEntityListener
import com.mapachos.pandoFarm.plants.engine.management.PlantEventListener
import com.mapachos.pandoFarm.plants.engine.seeds.command.SeedCommand
import com.mapachos.pandoFarm.plants.engine.seeds.listener.SeedListener
import com.mapachos.pandoFarm.player.culling.plant.PlayerPlantLookManager
import com.mapachos.pandoFarm.player.culling.plant.listener.PlantCullingListener
import com.mapachos.pandoFarm.player.management.PlayerDataManager
import com.mapachos.pandoFarm.util.config.ConfigPath
import com.mapachos.pandoFarm.economy.EconomyController
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class PandoFarm : JavaPlugin() {

    private lateinit var mysql: MySQLManager

    private lateinit var pStaticPlantsTable: StaticPlantTable

    private lateinit var pHarvestPlantTable: HarvestPlantTable

    private lateinit var pPlayerDataTable: PlayerDataTable

    private lateinit var pFarmSalesTable: FarmSalesTable

    private lateinit var pGlobalPlantRegistry: GlobalPlantRegistry

    override fun onEnable() {
        pInstance = this
        saveDefaultConfig()
        ensureDefaultConfigKeys()
        mySql()
        manager()
        startRegistries()
        listeners()
        reloadPlantsForOnlinePlayers()
        registerCommands()
    }

    private fun reloadPlantsForOnlinePlayers() {
        server.onlinePlayers.map { it.world }.toSet().forEach { world ->
            getGlobalPlantRegistry().loadPlantsOnWorld(world)
        }
    }

    private fun ensureDefaultConfigKeys(){
        val cfg = config
        var changed = false
        if(!cfg.isSet(ConfigPath.GROWTH_TASK_PERIOD_TICKS.path)){ cfg.set(ConfigPath.GROWTH_TASK_PERIOD_TICKS.path,20); changed=true }
        if(!cfg.isSet(ConfigPath.GROWTH_AGE_INCREMENT.path)){ cfg.set(ConfigPath.GROWTH_AGE_INCREMENT.path,1); changed=true }
        if(!cfg.isSet(ConfigPath.MARKET_DEMAND_RECALC_PERIOD_TICKS.path)){ cfg.set(ConfigPath.MARKET_DEMAND_RECALC_PERIOD_TICKS.path, 20L*60L*30L); changed=true } // 30 min
        if(!cfg.isSet(ConfigPath.MARKET_DEMAND_MIN_MULTIPLIER.path)){ cfg.set(ConfigPath.MARKET_DEMAND_MIN_MULTIPLIER.path,0.5); changed=true }
        if(!cfg.isSet(ConfigPath.MARKET_DEMAND_MAX_MULTIPLIER.path)){ cfg.set(ConfigPath.MARKET_DEMAND_MAX_MULTIPLIER.path,2.0); changed=true }
        // Player look engine defaults
        if(!cfg.isSet(ConfigPath.LOOK_PERIOD_TICKS.path)){ cfg.set(ConfigPath.LOOK_PERIOD_TICKS.path,5L); changed=true }
        if(changed) saveConfig()
    }

    private fun listeners() {
        registerListener(PlantCullingListener(this))
        registerListener(SeedListener(this))
        registerListener(PlantEventListener(this))
        registerListener(PlantEntityListener(this))
    }

    private fun manager() {
        PlayerDataManager.start(this)
        FarmMarketManager.loadAllMarkets(this)
        PlayerPlantLookManager.start(this)
        EconomyController.start(this)
    }

    private fun startRegistries() {
        HarvestEffectRegistry.start()
        pGlobalPlantRegistry = GlobalPlantRegistry(this)
        HarvestTypeRegistry.start()
        PlantTypeRegistry.start()
    }

    override fun onDisable() {
        // Stop all per-player look engines
        PlayerPlantLookManager.stop()
        FarmMarketManager.saveAllMarkets(this)
        if(this::pGlobalPlantRegistry.isInitialized){
            pGlobalPlantRegistry.shutdown(true)
        }
        ModelManager.clear()
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

    private fun registerCommands(){
        registerCommand("seed", SeedCommand())
        registerCommand("testplants", TestPlantsCommand.getCommand())
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
