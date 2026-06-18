package com.chattriggers.ctjs.api.entity

import com.chattriggers.ctjs.api.CTWrapper
import com.chattriggers.ctjs.api.render.GUIRenderer
import com.chattriggers.ctjs.internal.mixins.ParticleAccessor
import com.chattriggers.ctjs.internal.utils.asMixin
import net.minecraft.client.particle.Particle
import java.awt.Color

class CTParticle(override val mcValue: Particle) : CTWrapper<Particle> {
    private val mixed: ParticleAccessor = mcValue.asMixin()

    var x by mixed::x
    var y by mixed::y
    var z by mixed::z

    var lastX by mixed::xo
    var lastY by mixed::yo
    var lastZ by mixed::zo

    val renderX get() = lastX + (x - lastX) * GUIRenderer.partialTicks
    val renderY get() = lastY + (y - lastY) * GUIRenderer.partialTicks
    val renderZ get() = lastZ + (z - lastZ) * GUIRenderer.partialTicks

    var motionX by mixed::xd
    var motionY by mixed::yd
    var motionZ by mixed::zd

    var age by mixed::age
    var dead by mixed::removed

    fun scale(scale: Float) = apply {
        mcValue.scale(scale)
    }

    /**
     * Sets the color of the particle.
     * @param red the red value between 0 and 1.
     * @param green the green value between 0 and 1.
     * @param blue the blue value between 0 and 1.
     */
    @Deprecated("Deprecated since mojang does not have a similar method") // for now perhaps
    fun setColor(red: Float, green: Float, blue: Float) = apply {
    }

    /**
     * Sets the color of the particle.
     * @param red the red value between 0 and 1.
     * @param green the green value between 0 and 1.
     * @param blue the blue value between 0 and 1.
     * @param alpha the alpha value between 0 and 1.
     */
    @Deprecated("Deprecated since mojang does not have a similar method")
    fun setColor(red: Float, green: Float, blue: Float, alpha: Float) = apply {
    }

    @Deprecated("Deprecated since mojang does not have a similar method")
    fun setColor(color: Long) = apply {
    }

    /**
     * Sets the alpha of the particle.
     * @param alpha the alpha value between 0 and 1.
     */
    @Deprecated("Deprecated since mojang does not have a similar method")
    fun setAlpha(alpha: Float) = apply {
//        mixed.alpha = alpha
    }

    /**
     * Returns the color of the Particle
     *
     * @return A [Color] with the R, G, B and A values
     */
    @Deprecated("Deprecated since mojang does not have a similar method")
    fun getColor() = {
    }

    fun setColor(color: Color) = setColor(color.rgb.toLong())

    /**
     * Sets the amount of ticks this particle will live for
     *
     * @param maxAge the particle's max age (in ticks)
     */
    fun setMaxAge(maxAge: Int) = apply {
        mcValue.setLifetime(maxAge)
    }

    fun remove() = apply {
        mcValue.remove()
    }

    override fun toString() = "Particle(type=${mcValue.javaClass.simpleName}, pos=($x, $y, $z), age=$age)"
}
