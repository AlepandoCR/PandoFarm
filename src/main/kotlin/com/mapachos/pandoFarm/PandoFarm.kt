package com.mapachos.pandoFarm

import com.mapachos.pandoFarm.database.MySQLManager
import com.mapachos.pandoFarm.database.table.types.HarvestPlantTable
import com.mapachos.pandoFarm.database.table.types.StaticPlantTable
import com.mapachos.pandoFarm.plants.engine.management.PlantEventListener
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class PandoFarm : JavaPlugin() {

    private lateinit var mysql: MySQLManager

    private lateinit var pStaticPlantsTable: StaticPlantTable

    private lateinit var pHarvestPlantTable: HarvestPlantTable

    override fun onEnable() {
        pInstance = this
        mySql()
        registerListener(PlantEventListener)
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
    }

    fun registerListener(listener: Listener){
        server.pluginManager.registerEvents(listener,this)
    }

    fun getStaticPlantTable(): StaticPlantTable {
        if(!this::pStaticPlantsTable.isInitialized){
            throw IllegalStateException("StaticPlantTable is not initialized")
        }
        return pStaticPlantsTable
    }

    fun getHarvestPlantTable(): HarvestPlantTable {
        if(!this::pHarvestPlantTable.isInitialized){
            throw IllegalStateException("HarvestPlantTable is not initialized")
        }
        return pHarvestPlantTable
    }

    override fun onDisable() {
        mysql.disconnect()
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
