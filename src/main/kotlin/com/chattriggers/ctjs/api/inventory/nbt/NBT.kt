package com.chattriggers.ctjs.api.inventory.nbt

import com.chattriggers.ctjs.internal.utils.getOption
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.NativeObject

object NBT {
    /**
     * Creates a new [NBTBase] from the given [nbt]
     *
     * @param nbt the value to convert to NBT
     * @param options optional argument to allow refinement of the NBT data.
     * Possible options include:
     * - coerceNumericStrings: Boolean, default false.
     * E.g. "10b" as a byte, "20s" as a short, "30f" as a float, "40d" as a double,
     * "50l" as a long
     * - preferArraysOverLists: Boolean, default false
     * E.g. a list with all bytes or integers will be converted to an NBTTagByteArray or
     * NBTTagIntArray accordingly
     *
     * @return [NBTTagCompound] if [nbt] is an object, [NBTTagList] if [nbt]
     * is an array and preferArraysOverLists is false, or [NBTBase] otherwise.
     */
    @JvmStatic
    @JvmOverloads
    fun parse(nbt: Any, options: NativeObject? = null): NBTBase {
        return when (nbt) {
            is NativeObject -> NBTTagCompound(nbt.toNBT(options) as CompoundTag)
            is NativeArray -> {
                nbt.toNBT(options).let {
                    if (it is ListTag) {
                        NBTTagList(it)
                    } else {
                        NBTBase(it)
                    }
                }
            }
            else -> NBTBase(nbt.toNBT(options))
        }
    }

    @JvmStatic
    fun toObject(nbt: NBTTagCompound): NativeObject = nbt.toObject()

    @JvmStatic
    fun toArray(nbt: NBTTagList): NativeArray = nbt.toArray()

    private fun Any.toNBT(options: NativeObject?): Tag {
        val preferArraysOverLists = options.getOption<Boolean>("preferArraysOverLists", false)
        val coerceNumericStrings = options.getOption<Boolean>("coerceNumericStrings", false)

        return when (this) {
            is NativeObject -> CompoundTag().apply {
                entries.forEach { entry ->
                    put(entry.key.toString(), entry.value.toNBT(options))
                }
            }
            is NativeArray -> {
                val normalized = map { it?.toNBT(options) }

                if (!preferArraysOverLists || normalized.isEmpty()) {
                    return ListTag().apply { addAll(normalized) }
                }

                return when {
                    (normalized.all { it is ByteTag }) -> {
                        ByteArrayTag(normalized.map { (it as ByteTag).byteValue() }.toByteArray())
                    }

                    (normalized.all { it is IntTag }) -> {
                        IntArrayTag(normalized.map { (it as IntTag).intValue() }.toIntArray())
                    }

                    (normalized.all { it is LongTag }) -> {
                        LongArrayTag(normalized.map { (it as LongTag).longValue() }.toLongArray())
                    }

                    else -> ListTag().apply { addAll(normalized) }
                }
            }
            is Boolean -> ByteTag.valueOf(if (this) 1 else 0)
            is CharSequence -> parseString(this.toString(), coerceNumericStrings)
            is Byte -> ByteTag.valueOf(this)
            is Short -> ShortTag.valueOf(this)
            is Int -> IntTag.valueOf(this)
            is Long -> LongTag.valueOf(this)
            is Float -> FloatTag.valueOf(this)
            is Double -> DoubleTag.valueOf(this)
            else -> throw IllegalArgumentException("Invalid NBT. Value provided: $this")
        }
    }

    private val numberNBTFormat = Regex("^([+-]?\\d+\\.?\\d*)([bslfd])?\$", RegexOption.IGNORE_CASE)

    private fun parseString(nbtData: String, coerceNumericStrings: Boolean): Tag {
        if (!coerceNumericStrings) {
            return StringTag.valueOf(nbtData)
        }

        val res = numberNBTFormat.matchEntire(nbtData)?.groupValues ?: return StringTag.valueOf(nbtData)

        val number = res[1]
        val suffix = res[2]

        return when (suffix.lowercase()) {
            "" -> {
                if (number.contains(".")) {
                    DoubleTag.valueOf(number.toDouble())
                } else {
                    IntTag.valueOf(number.toInt())
                }
            }
            "b" -> ByteTag.valueOf(number.toByte())
            "s" -> ShortTag.valueOf(number.toShort())
            "l" -> LongTag.valueOf(number.toLong())
            "f" -> FloatTag.valueOf(number.toFloat())
            "d" -> DoubleTag.valueOf(number.toDouble())
            else -> StringTag.valueOf(nbtData)
        }
    }
}
