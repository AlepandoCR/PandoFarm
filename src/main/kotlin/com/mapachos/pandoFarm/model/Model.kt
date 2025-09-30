package com.mapachos.pandoFarm.model

import com.mapachos.pandoFarm.model.preset.ModelPreset
import com.mapachos.pandoFarm.model.util.ModelManager
import com.mapachos.pandoFarm.model.util.RendererSupplier
import com.mapachos.pandoFarm.model.util.RendererSupplier.resize
import kr.toxicity.model.api.bone.RenderedBone
import kr.toxicity.model.api.tracker.EntityHideOption
import kr.toxicity.model.api.tracker.ModelScaler
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.function.Predicate

class Model<T : Entity>(
    private val name: String,
    private val location: Location,
    private val entityClass: Class<T>
) {
    private val renderer = RendererSupplier.get(name)
    val world: World = location.world
    val entity: T = world.spawn(location, entityClass)
    val tracker = renderer.create(entity)
    private val originalModelScale = tracker.scaler().scale(tracker)

    init {
        ModelManager.register(this)
    }


    fun animate(animationName: String){
        tracker.animate(animationName)
    }

    fun rotate(){
        TODO()
    }

    fun scale(value: Float){
        scaleWithEntity(value)
    }

    fun resetScale(){
        tracker.scaler(ModelScaler.value(originalModelScale))
    }

    private fun scaleWithEntity(value: Float){
        if(entity !is LivingEntity) return

        val livingEntity = entity as LivingEntity

        livingEntity.resize(value.toDouble())

        tracker.scaler(ModelScaler.entity())
        tracker.updateBaseEntity()
    }

    fun teleport(location: Location){
        entity.teleport(location)
        tracker.updateBaseEntity()
    }

    fun damageTint(value: Int){
        tracker.damageTintValue(value)
    }

    fun remove(){
        ModelManager.unregister(this)
        tracker.close()
        tracker.despawn()
        entity.remove()
    }

    fun hide(player: Player){
        tracker.hide(player)
    }

    fun pause(value: Boolean){
        tracker.pause(value)
    }

    fun hideBase(value: Boolean){
        if(value) tracker.hideOption(EntityHideOption.DEFAULT)
        else tracker.hideOption(EntityHideOption.FALSE)
    }

    fun getLocation(): Location{
        return tracker.location()
    }

    fun setGlowing(rgb: Int) {
        for (bone in tracker.bones()) {
            val renderedBonePredicate = Predicate.isEqual<RenderedBone>(bone)
            bone.glow(renderedBonePredicate,true)
            bone.glowColor(renderedBonePredicate, rgb)
        }
    }

    fun stopGlowing() {
        for (bone in tracker.bones()) {
            val renderedBonePredicate = Predicate.isEqual<RenderedBone>(bone)
            bone.glow(renderedBonePredicate,false)
        }
    }

    fun enchant() {
        for (bone in tracker.bones()) {
            val renderedBonePredicate = Predicate.isEqual<RenderedBone>(bone)
            bone.enchant(renderedBonePredicate,true)
        }
    }

    fun unenchant(){
        for (bone in tracker.bones()) {
            val renderedBonePredicate = Predicate.isEqual<RenderedBone>(bone)
            bone.enchant(renderedBonePredicate,false)
        }
    }

    fun billboard(billboard: Billboard){
        for (bone in tracker.bones()) {
            val renderedBonePredicate = Predicate.isEqual<RenderedBone>(bone)
            bone.billboard(renderedBonePredicate,billboard)
        }
    }

    companion object{
        fun <A: Entity>fromPreset(preset: ModelPreset<A>, world: World, location: Location): Model<A>{
            return preset.buildModel(location)
        }
    }
}
