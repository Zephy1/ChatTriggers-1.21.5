package com.chattriggers.ctjs.api.entity

import com.chattriggers.ctjs.api.CTWrapper
import com.chattriggers.ctjs.api.world.block.CTBlock
import com.chattriggers.ctjs.api.world.block.CTBlockPos
import com.chattriggers.ctjs.api.world.block.CTBlockType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

class CTBlockEntity(override val mcValue: BlockEntity) : CTWrapper<BlockEntity> {
    fun getX(): Int = getBlockPos().x

    fun getY(): Int = getBlockPos().y

    fun getZ(): Int = getBlockPos().z

    //#if MC<=12111
    //$$fun getBlockType(): CTBlockType = CTBlockType(BlockEntityType.getKey(mcValue.type)!!.toString())
    //#else
    fun getBlockType(): CTBlockType = CTBlockType(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(mcValue.type)!!.toString())
    //#endif

    fun getBlockPos(): CTBlockPos = CTBlockPos(mcValue.blockPos)

    fun getBlock(): CTBlock = CTBlock(getBlockType(), getBlockPos())

    override fun toString(): String = "BlockEntity(type=${getBlockType()}, pos=[${getX()}, ${getY()}, ${getZ()}])"
}
