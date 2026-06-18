package com.chattriggers.ctjs.api.entity

import com.chattriggers.ctjs.api.inventory.CTItem
import com.chattriggers.ctjs.api.world.PotionEffect
import com.chattriggers.ctjs.api.world.PotionEffectType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity

open class CTLivingEntity(override val mcValue: LivingEntity) : CTEntity(mcValue) {
    fun getActivePotionEffects(): List<PotionEffect> {
        return mcValue.activeEffects.map(::PotionEffect)
    }

    fun canSeeEntity(other: Entity) = mcValue.hasLineOfSight(other)

    fun canSeeEntity(other: CTEntity) = canSeeEntity(other.toMC())

    /**
     * Gets the item currently in the entity's specified inventory slot.
     * 0 for main hand, 1 for offhand, 2-5 for armor
     *
     * @param slot the slot to access
     * @return the item in said slot
     */
    fun getStackInSlot(slot: Int): CTItem? {
        return mcValue.getItemBySlot(EquipmentSlot.entries[slot]).let(CTItem::fromMC)
    }

    fun getHP() = mcValue.health

    fun getMaxHP() = mcValue.maxHealth

    fun getAbsorption() = mcValue.absorptionAmount

    fun getAge() = mcValue.tickCount

    fun getArmorValue() = mcValue.armorValue

    fun isPotionActive(id: Int) = mcValue.hasEffect(BuiltInRegistries.MOB_EFFECT.get(id).get())

    fun isPotionActive(type: PotionEffectType) = mcValue.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(type.type))

    fun isPotionActive(effect: PotionEffect) = isPotionActive(effect.type)
}
