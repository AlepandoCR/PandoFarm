package com.mapachos.pandoFarm.model.util

import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.data.renderer.ModelRenderer
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

object RendererSupplier {
    private val betterModel = BetterModel.plugin()
    fun get(name: String): ModelRenderer{
        val renderer = betterModel.modelManager().model(name) ?: throw NullPointerException("model name is not registered")
        return renderer
    }

    fun LivingEntity.resize(value: Double){
        val attribute = this.getAttribute(Attribute.SCALE)
        attribute?.let {
            it.baseValue = value
        }
    }
}