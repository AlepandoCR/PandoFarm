package com.mapachos.pandoFarm.plants.engine.harvest

import com.google.common.collect.Multimap
import net.kyori.adventure.text.Component
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.components.CustomModelDataComponent
import org.bukkit.persistence.PersistentDataContainer

class HarvestItem(val harvest: Harvest<out HarvestType>) {

    private lateinit var serializedItem: ByteArray

    private fun lore(): List<Component>{
        TODO("Display the harvest data in the lore")
        return listOf()
    }

    private fun persistentDataContainer(persistentDataContainer: PersistentDataContainer){
        TODO("Serialize the harvest data into the persistent data container")
    }

    private fun customModelDataComponent(customModelDataComponent: CustomModelDataComponent){
        customModelDataComponent.strings.add(harvest.harvestType.customModelComponentString)
    }

    private fun attributeModifiers(map: Multimap<Attribute, AttributeModifier>?){

    }

    fun buildItem(): ItemStack{
        val item = ItemStack(harvest.harvestType.material)
        val meta = item.itemMeta ?: return item
        decorators(meta)
        data(meta)

        return item
    }

    fun data(meta: ItemMeta) {
        persistentDataContainer(meta.persistentDataContainer)
        customModelDataComponent(meta.customModelDataComponent)
        attributeModifiers(meta.attributeModifiers)
    }

    private fun decorators(meta: ItemMeta) {
        meta.displayName(harvest.harvestType.componentName)
        meta.lore(lore())
    }

    fun bytes(): ByteArray{
        if(!::serializedItem.isInitialized){
            serializedItem = buildItem().serializeAsBytes()
        }
        return serializedItem
    }
}