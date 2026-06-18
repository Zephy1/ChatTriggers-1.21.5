package com.chattriggers.ctjs.api.inventory

import com.chattriggers.ctjs.api.CTWrapper
import com.chattriggers.ctjs.api.message.TextComponent
import com.chattriggers.ctjs.api.world.block.CTBlockType
import com.chattriggers.ctjs.internal.utils.toIdentifier
import net.minecraft.world.item.Items
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item

class ItemType(override val mcValue: Item) : CTWrapper<Item> {
    init {
        require(mcValue !== Items.AIR) {
            "Can not wrap air as an ItemType"
        }
    }

    constructor(itemName: String) : this(BuiltInRegistries.ITEM.get(itemName.toIdentifier()).get().value())

    constructor(id: Int) : this(BuiltInRegistries.ITEM.byId(id))

    constructor(blockType: CTBlockType) : this(blockType.toMC().asItem())

    fun getName(): String = getNameComponent().formattedText

    fun getNameComponent(): TextComponent = TextComponent(mcValue.toString())

    fun getId(): Int = Item.getId(mcValue)

    fun getTranslationKey(): String = mcValue.descriptionId

    fun getRegistryName(): String = BuiltInRegistries.ITEM.getKey(mcValue).toString()

    fun asItem(): CTItem = CTItem(this)

    companion object {
        @JvmStatic
        fun fromMC(mcValue: Item): ItemType? {
            return if (mcValue === Items.AIR) {
                null
            } else {
                ItemType(mcValue)
            }
        }
    }
}
