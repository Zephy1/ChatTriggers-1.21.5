package com.chattriggers.ctjs.api.world.block

import com.chattriggers.ctjs.api.client.CTPlayer
import com.chattriggers.ctjs.api.inventory.CTItem
import com.chattriggers.ctjs.api.world.World

/**
 * An immutable reference to a placed block in the world. It
 * has a block type, a position, and optionally a specific face.
 */
open class CTBlock(
    val type: CTBlockType,
    val pos: CTBlockPos,
    val face: BlockFace? = null,
) {
    val x: Int get() = pos.x
    val y: Int get() = pos.y
    val z: Int get() = pos.z

    fun withType(type: CTBlockType) = CTBlock(type, pos, face)

    fun withPos(pos: CTBlockPos) = CTBlock(type, pos, face)

    /**
     * Narrows this block to reference a certain face. Used by
     * [CTPlayer.lookingAt] to specify the block face
     * being looked at.
     */
    fun withFace(face: BlockFace) = CTBlock(type, pos, face)

    fun getState() = World.toMC()?.getBlockState(pos.toMC())

    @JvmOverloads
    fun isEmittingPower(face: BlockFace? = null): Boolean {
        if (face != null) return World.toMC()!!.hasSignal(pos.toMC(), face.toMC())
        return BlockFace.entries.any { isEmittingPower(it) }
    }

    @JvmOverloads
    fun getEmittingPower(face: BlockFace? = null): Int {
        if (face != null) return World.toMC()!!.getSignal(pos.toMC(), face.toMC())
        return BlockFace.entries.asSequence().map(::getEmittingPower).firstOrNull { it != 0 } ?: 0
    }

    fun isReceivingPower() = World.toMC()!!.hasNeighborSignal(pos.toMC())

    fun getReceivingPower() = World.toMC()!!.getBestNeighborSignal(pos.toMC())

    /**
     * Checks whether the block can be mined with the tool in the player's hand
     *
     * @return whether the block can be mined
     */
    fun canBeHarvested(): Boolean = CTPlayer.getHeldItem()?.let(::canBeHarvestedWith) ?: false

    fun canBeHarvestedWith(item: CTItem): Boolean = item.canHarvest(this)

    override fun toString() = "Block{type=$type, pos=($x, $y, $z), face=$face}"
}
