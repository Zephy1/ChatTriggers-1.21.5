package com.chattriggers.ctjs.api.client

object MathLib {
    /**
     * Maps a number from one range to another.
     *
     * @param number the number to map
     * @param inMin the original range min
     * @param inMax the original range max
     * @param outMin the final range min
     * @param outMax the final range max
     * @return the re-mapped number
     */
    @JvmStatic
    fun map(number: Float, inMin: Float, inMax: Float, outMin: Float, outMax: Float): Float {
        return (number - inMin) * (outMax - outMin) / (inMax - inMin) + outMin
    }

    /**
     * Clamps a floating number between two values.
     *
     * @param number the number to clamp
     * @param min the minimum
     * @param max the maximum
     * @return the clamped number
     */
    @JvmStatic
    fun clampFloat(number: Float, min: Float, max: Float): Float {
        return number.coerceIn(min, max)
    }

    /**
     * Clamps an integer number between two values.
     *
     * @param number the number to clamp
     * @param min the minimum
     * @param max the maximum
     * @return the clamped number
     */
    @JvmStatic
    fun clamp(number: Int, min: Int, max: Int): Int {
        return number.coerceIn(min, max)
    }

    @JvmStatic
    fun wrapAngleTo180(value: Double): Double = (value % 360.0).let {
        when {
            it >= 180.0 -> it - 360.0
            it <= -180.0 -> it + 360.0
            else -> it
        }
    }
}
