package com.chattriggers.ctjs.api.world

import com.chattriggers.ctjs.api.CTWrapper
import com.chattriggers.ctjs.api.entity.CTBlockEntity
import com.chattriggers.ctjs.api.entity.CTEntity
import com.chattriggers.ctjs.internal.mixins.ChunkAccessAccessor
import com.chattriggers.ctjs.internal.utils.asMixin
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.phys.AABB

// TODO: Add more methods here?
class CTChunk(override val mcValue: ChunkAccess) : CTWrapper<ChunkAccess> {
    /**
     * Gets the x position of the chunk
     */
    fun getX() = mcValue.pos.x

    /**
     * Gets the z position of the chunk
     */
    fun getZ() = mcValue.pos.z

    /**
     * Gets the minimum x coordinate of a block in the chunk
     *
     * @return the minimum x coordinate
     */
    fun getMinBlockX() = getX() * 16

    /**
     * Gets the minimum z coordinate of a block in the chunk
     *
     * @return the minimum z coordinate
     */
    fun getMinBlockZ() = getZ() * 16

    /**
     * Gets every entity in this chunk
     *
     * @return the entity list
     */
    fun getAllEntities(): List<CTEntity> = getAllEntitiesOfType(Entity::class.java)

    /**
     * Gets every entity in this chunk of a certain class
     *
     * @param clazz the class to filter for (Use `Java.type().class` to get this)
     * @return the entity list
     */
    fun getAllEntitiesOfType(clazz: Class<Entity>): List<CTEntity> {
        val box = AABB(
            BlockPos(getMinBlockX(), mcValue.minY, getMinBlockZ()),
        ).expandTowards(16.0, mcValue.maxY.toDouble(), 16.0)

        return World.toMC()?.getEntitiesOfClass(clazz, box) { true }?.map(CTEntity::fromMC) ?: listOf()
    }

    /**
     * Gets every block entity in this chunk
     *
     * @return the block entity list
     */
    fun getAllBlockEntities(): List<CTBlockEntity> = mcValue.asMixin<ChunkAccessAccessor>().blockEntities.values.map(::CTBlockEntity)

    /**
     * Gets every block entity in this chunk of a certain class
     *
     * @param clazz the class to filter for (Use `Java.type().class` to get this)
     * @return the block entity list
     */
    fun getAllBlockEntitiesOfType(clazz: Class<*>): List<CTBlockEntity> {
        return getAllBlockEntities().filter {
            clazz.isInstance(it.toMC())
        }
    }
}
