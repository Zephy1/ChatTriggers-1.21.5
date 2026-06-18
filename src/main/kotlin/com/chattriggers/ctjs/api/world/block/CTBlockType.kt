package com.chattriggers.ctjs.api.world.block

import com.chattriggers.ctjs.api.CTWrapper
import com.chattriggers.ctjs.api.inventory.CTItem
import com.chattriggers.ctjs.api.inventory.ItemType
import com.chattriggers.ctjs.api.message.TextComponent
import com.chattriggers.ctjs.internal.utils.toIdentifier
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

/**
 * An immutable wrapper around Minecraft's Block object. Note
 * that this references a block "type", and not an actual block
 * in the world. If a reference to a particular block is needed,
 * use [CTBlock]
 */
class CTBlockType(override val mcValue: Block) : CTWrapper<Block> {
    constructor(block: CTBlockType) : this(block.mcValue)

    constructor(blockName: String) : this(BuiltInRegistries.BLOCK[blockName.toIdentifier()].get().value())

    constructor(blockID: Int) : this(ItemType(Item.byId(blockID)).getRegistryName())

    constructor(item: CTItem) : this(Block.byItem(item.mcValue.item))

    /**
     * Returns a [CTBlock] based on this block and the
     * provided BlockPos
     *
     * @param blockPos the block position
     * @return a [CTBlock] object
     */
    fun withBlockPos(blockPos: CTBlockPos) = CTBlock(this, blockPos)

    fun getID(): Int = BuiltInRegistries.BLOCK.indexOf(mcValue)

    /**
     * Gets the block's registry name.
     * Example: minecraft:oak_planks
     *
     * @return the block's registry name
     */
    fun getRegistryName(): String = BuiltInRegistries.BLOCK.getKey(mcValue).toString()

    /**
     * Gets the block's translation key.
     * Example: block.minecraft.oak_planks
     *
     * @return the block's translation key
     */
    fun getTranslationKey(): String = mcValue.descriptionId

    /**
     * Gets the block's localized name.
     * Example: Wooden Planks
     *
     * @return the block's localized name
     */
    fun getName() = TextComponent(mcValue.name).formattedText

    fun getLightValue(): Int = getDefaultState().lightEmission

    fun getDefaultState() = mcValue.defaultBlockState()

    fun canProvidePower() = getDefaultState().isSignalSource

    fun isTranslucent() = getDefaultState().useShapeForLightOcclusion()

    override fun toString(): String = "BlockType{${getRegistryName()}}"
}
