package com.chattriggers.ctjs.api.inventory.nbt

import com.chattriggers.ctjs.api.CTWrapper
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.NativeObject

open class NBTBase(override val mcValue: Tag) : CTWrapper<Tag> {
    /**
     * Gets the type byte for the tag.
     */
    val id: Byte
        get() = mcValue.id

    /**
     * Creates a clone of the tag.
     */
    fun copy() = mcValue.copy()

    /**
     * Return whether this compound has no tags.
     */
    fun hasNoTags() = when (this) {
        is NBTTagCompound -> tagMap.isEmpty()
        is NBTTagList -> mcValue.isEmpty
        else -> false
    }

    fun hasTags() = !hasNoTags()

    override fun equals(other: Any?) = mcValue == other

    override fun hashCode() = mcValue.hashCode()

    override fun toString() = mcValue.toString()

    companion object {
        @JvmStatic
        fun fromMC(nbt: Tag): NBTBase = when (nbt) {
            is CompoundTag -> NBTTagCompound(nbt)
            is ListTag -> NBTTagList(nbt)
            else -> NBTBase(nbt)
        }

        fun Tag.toObject(): Any? {
            return when (this) {
                is StringTag -> asString()
                is ByteTag -> byteValue()
                is ShortTag -> shortValue()
                is IntTag -> intValue()
                is LongTag -> longValue()
                is FloatTag -> floatValue()
                is DoubleTag -> doubleValue()
                is CompoundTag -> toObject()
                is ListTag -> toObject()
                is ByteArrayTag -> NativeArray(asByteArray.toTypedArray()).expose()
                is IntArrayTag -> NativeArray(asIntArray.toTypedArray()).expose()
                else -> error("Unknown tag type $javaClass")
            }
        }

        fun CompoundTag.toObject(): NativeObject {
            val o = NativeObject()
            o.expose()

            for (key in keySet()) {
                val value = this[key]
                if (value != null) {
                    o.put(key, o, value.toObject())
                }
            }

            return o
        }

        fun ListTag.toObject(): NativeArray {
            val tags = mutableListOf<Any?>()
            for (i in 0 until count()) {
                tags.add(get(i).toObject())
            }
            val array = NativeArray(tags.toTypedArray())
            array.expose()
            return array
        }

        private fun NativeArray.expose() = apply {
            // Taken from the private NativeArray#init method
            exportAsJSClass(32, this, false)
        }

        private fun NativeObject.expose() = apply {
            // Taken from the private NativeObject#init method
            exportAsJSClass(12, this, false)
        }
    }
}
