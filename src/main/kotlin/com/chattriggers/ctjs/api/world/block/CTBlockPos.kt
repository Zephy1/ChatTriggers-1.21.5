package com.chattriggers.ctjs.api.world.block

import com.chattriggers.ctjs.api.CTWrapper
import com.chattriggers.ctjs.api.entity.CTEntity
import com.chattriggers.ctjs.api.vec.Vec3i
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import kotlin.math.floor
import kotlin.math.sqrt

class CTBlockPos(x: Int, y: Int, z: Int) : Vec3i(x, y, z), CTWrapper<BlockPos> {
    override val mcValue = BlockPos(x, y, z)

    constructor(x: Number, y: Number, z: Number) : this(
        floor(x.toDouble()).toInt(),
        floor(y.toDouble()).toInt(),
        floor(z.toDouble()).toInt(),
    )

    constructor(pos: Vec3i) : this(pos.x, pos.y, pos.z)

    constructor(pos: BlockPos) : this(pos.x, pos.y, pos.z)

    constructor(source: CTEntity) : this(source.getBlockPos())

    override fun translated(dx: Int, dy: Int, dz: Int) = CTBlockPos(super.translated(dx, dy, dz))

    override fun scaled(scale: Int) = CTBlockPos(super.scaled(scale))

    override fun scaled(xScale: Int, yScale: Int, zScale: Int) = CTBlockPos(super.scaled(xScale, yScale, zScale))

    override fun crossProduct(other: Vec3i) = CTBlockPos(super.crossProduct(other))

    override operator fun unaryMinus() = CTBlockPos(super.unaryMinus())

    override operator fun plus(other: Vec3i) = CTBlockPos(super.plus(other))

    override operator fun minus(other: Vec3i) = CTBlockPos(super.minus(other))

    @JvmOverloads
    fun up(n: Int = 1) = offset(BlockFace.UP, n)

    @JvmOverloads
    fun down(n: Int = 1) = offset(BlockFace.DOWN, n)

    @JvmOverloads
    fun north(n: Int = 1) = offset(BlockFace.NORTH, n)

    @JvmOverloads
    fun south(n: Int = 1) = offset(BlockFace.SOUTH, n)

    @JvmOverloads
    fun east(n: Int = 1) = offset(BlockFace.EAST, n)

    @JvmOverloads
    fun west(n: Int = 1) = offset(BlockFace.WEST, n)

    @JvmOverloads
    fun offset(facing: BlockFace, n: Int = 1): CTBlockPos = CTBlockPos(x + facing.getOffsetX() * n, y + facing.getOffsetY() * n, z + facing.getOffsetZ() * n)

    fun distanceTo(other: CTBlockPos): Double {
        val x = (mcValue.x - other.x).toDouble()
        val y = (mcValue.y - other.y).toDouble()
        val z = (mcValue.z - other.z).toDouble()
        return sqrt(x * x + y * y + z * z)
    }

    fun toVec3d() = Vec3(x.toDouble(), y.toDouble(), z.toDouble())
}
