package com.mapachos.pandoFarm.plants.engine.harvest

import com.mapachos.pandoFarm.PandoFarm
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.components.CustomModelDataComponent
import org.bukkit.persistence.PersistentDataContainer

class HarvestItem(val harvest: Harvest) {

    private lateinit var serializedItem: ByteArray
    val dto = harvest.toDto()

    private fun lore(): List<Component>{
        return listOf(
            Component.text("Quality: ${dto.quality}"),
            Component.text("Effect: ${dto.harvestType.effect.description}"),
        )
    }

    private fun persistentDataContainer(persistentDataContainer: PersistentDataContainer){
        dto.applyOnPersistentDataContainer(persistentDataContainer)
    }

    private fun customModelDataComponent(customModelDataComponent: CustomModelDataComponent){
        val key = harvest.harvestType.customModelComponentString
        try {
            customModelDataComponent.strings.add(key)
        } catch (ex: UnsupportedOperationException){
            PandoFarm.getInstance().logger.fine("[HarvestItem] Unable to mutate CustomModelDataComponent.strings for '$key' (immutable list). Skipping.")
        } catch (ex: Exception){
            PandoFarm.getInstance().logger.warning("[HarvestItem] Failed to append custom model data string '$key': ${ex.message}")
        }
    }


    fun buildItem(): ItemStack{
        val item = ItemStack(harvest.harvestType.material)
        val meta = item.itemMeta ?: return item
        decorators(meta)
        data(meta)

        item.itemMeta = meta

        return item
    }

    fun data(meta: ItemMeta) {
        persistentDataContainer(meta.persistentDataContainer)
        customModelDataComponent(meta.customModelDataComponent)
    }

    private fun decorators(meta: ItemMeta) {
        meta.displayName(Component.text(harvest.harvestType.name))
        meta.lore(lore())
    }

    fun bytes(): ByteArray{
        if(!::serializedItem.isInitialized){
            serializedItem = buildItem().serializeAsBytes()
        }
        return serializedItem
    }
}