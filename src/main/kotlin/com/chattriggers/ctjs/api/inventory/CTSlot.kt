package com.chattriggers.ctjs.api.inventory

import com.chattriggers.ctjs.api.CTWrapper
import net.minecraft.world.inventory.Slot

class CTSlot(override val mcValue: Slot) : CTWrapper<Slot> {
    val index by mcValue::index

    val displayX by mcValue::x

    val displayY by mcValue::y

    val inventory get() = Inventory(mcValue.container)

    val item get(): CTItem? = CTItem.fromMC(mcValue.item)

    val isEnabled get() = mcValue.isActive

    override fun toString() = "Slot(inventory=$inventory, index=$index, item=$item)"
}
