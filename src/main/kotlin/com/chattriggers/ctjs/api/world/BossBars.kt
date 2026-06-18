package com.chattriggers.ctjs.api.world

import com.chattriggers.ctjs.api.CTWrapper
import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.api.message.TextComponent
import com.chattriggers.ctjs.internal.mixins.BossHealthOverlayAccessor
import com.chattriggers.ctjs.internal.utils.asMixin
import com.chattriggers.ctjs.internal.utils.getOption
import net.minecraft.client.gui.components.LerpingBossEvent
import net.minecraft.world.BossEvent
import org.mozilla.javascript.NativeObject
import java.util.UUID

object BossBars {
    @JvmStatic
    //#if MC<26.2
    //$$fun toMC() = Client.getMinecraft().gui.bossOverlay
    //#else
    fun toMC() = Client.getMinecraft().gui.hud.bossOverlay
    //#endif

    /**
     * Gets the list of currently shown [BossBar]s
     *
     * @return the currently displayed [BossBar]s
     */
    @JvmStatic
    fun getBossBars(): List<BossBar> = toMC().asMixin<BossHealthOverlayAccessor>().events.values.map(::BossBar)

    /**
     * Gets all [BossBar]s with a given name
     *
     * @param name the name to match
     * @return the [BossBar]s
     */
    @JvmStatic
    fun getBossBarsByName(name: String): List<BossBar> = getBossBars().filter { it.getName() == name }

    /**
     * Adds a new [BossBar] to be displayed
     *
     * Takes a parameter with the following options:
     * - name: The name to appear above the BossBar. Defaults to an empty string
     * - percent: The percent full the BossBar is. Defaults to 1 (full health)
     * - color: The color of the BossBar. Can be any [CTBossBarColor], but defaults to white
     * - sections: The number of notches/sections to appear on the BossBar. Can be any [CTBossBarStyle], but
     *             defaults to 1 entire section
     * - darkenSky: Whether the BossBar should darken the screen of the player. Defaults to false
     * - dragonMusic: Whether the BossBar should play dragon music while in the End. Defaults to false
     * - thickenFog: Whether the BossBar should thicken the fog around the player. Defaults to false
     *
     * @param obj An options bag
     *
     * @return the [BossBar] for further modification
     */
    @JvmStatic
    fun addBossBar(obj: NativeObject): BossBar {
        val name = obj.getOption<String>("name", "")
        val percent = obj.getOption<Float>("percent", 1f).coerceIn(0f..1f)
        val color = CTBossBarColor.from(obj.getOption("color", CTBossBarColor.WHITE))
        val style = CTBossBarStyle.from(obj.getOption("sections", CTBossBarStyle.ONE))
        val shouldDarkenSky = obj.getOption<Boolean>("darkenSky", false)
        val dragonMusic = obj.getOption<Boolean>("dragonMusic", false)
        val shouldThickenFog = obj.getOption<Boolean>("thickenFog", false)

        val uuid = UUID.randomUUID()

        val bossBar = LerpingBossEvent(
            uuid,
            TextComponent(name),
            percent,
            color.toMC(),
            style.toMC(),
            shouldDarkenSky,
            dragonMusic,
            shouldThickenFog,
        )

        toMC().asMixin<BossHealthOverlayAccessor>().events[uuid] = bossBar

        return BossBar(bossBar)
    }

    /**
     * Clears all [BossBar]s on screen
     */
    @JvmStatic
    fun clearBossBars() {
        toMC().reset()
    }

    /**
     * Removes all [BossBar]s with the given name
     *
     * @param name the name to match
     */
    @JvmStatic
    fun removeBossBarsByName(name: String) {
        toMC().asMixin<BossHealthOverlayAccessor>().events.values.removeIf {
            TextComponent(it.name).formattedText == ChatLib.addColor(name)
        }
    }

    /**
     * Removes the given [BossBar]
     *
     * @param bossBar the BossBar to remove
     */
    @JvmStatic
    fun removeBossBar(bossBar: BossBar) {
        toMC().asMixin<BossHealthOverlayAccessor>().events.remove(bossBar.getUUID())
    }

    class BossBar(override val mcValue: LerpingBossEvent) : CTWrapper<LerpingBossEvent> {
        /**
         * Gets the UUID of this BossBar
         *
         * @return the uuid
         */
        fun getUUID(): UUID = mcValue.id

        /**
         * Gets the name of this BossBar
         *
         * @return the name
         */
        fun getName(): String = TextComponent(mcValue.name).formattedText

        /**
         * Sets the name of this BossBar
         *
         * @param name the name to set
         */
        fun setName(name: String) = apply {
            mcValue.name = TextComponent(name)
        }

        /**
         * Gets how full this BossBar is
         *
         * @return how full the BossBar is
         */
        fun getPercent(): Float = mcValue.progress

        /**
         * Sets how full this BossBar is
         *
         * @param percent how full to set this BossBar. Must be between 0 and 1
         */
        fun setPercent(percent: Float) = apply {
            mcValue.setProgress(percent.coerceIn(0f..1f))
        }

        /**
         * Gets the [CTBossBarColor] of this BossBar
         */
        fun getColor(): CTBossBarColor = CTBossBarColor.fromMC(mcValue.color)

        /**
         * Sets the [CTBossBarColor] of this BossBar
         *
         * @param color the color to set. Can be [CTBossBarColor], [BossEvent.BossBarColor], or a string
         */
        fun setColor(color: Any) = apply {
            mcValue.color = CTBossBarColor.from(color).toMC()
        }

        /**
         * Gets the style of this BossBar. e.g. how many notches are displayed
         */
        fun getStyle(): CTBossBarStyle = CTBossBarStyle.fromMC(mcValue.overlay)

        /**
         * Sets the style of this BossBar
         *
         * @param style the style to set. Can be [CTBossBarStyle], [BossEvent.BossBarOverlay], a string,
         * or a number of how many notches to put
         */
        fun setStyle(style: Any) = apply {
            mcValue.setOverlay(CTBossBarStyle.from(style).toMC())
        }

        /**
         * Gets whether this BossBar darkens the sky
         */
        fun shouldDarkenSky(): Boolean = mcValue.shouldDarkenScreen()

        /**
         * Sets whether this BossBar should darken the sky
         *
         * @param darken whether to darken the sky
         */
        fun setShouldDarkenSky(darken: Boolean) = apply {
            mcValue.setDarkenScreen(darken)
        }

        /**
         * Gets whether this BossBar will play dragon music.
         * This will do nothing when the player is not in the end dimension
         */
        fun hasDragonMusic(): Boolean = mcValue.shouldPlayBossMusic()

        /**
         * Sets whether this BossBar will play dragon music
         *
         * @param music whether to play dragon music
         */
        fun setHasDragonMusic(music: Boolean) = apply {
            mcValue.setPlayBossMusic(music)
        }

        /**
         * Gets whether this BossBar should thicken the fog around the player
         */
        fun shouldThickenFog(): Boolean = mcValue.shouldCreateWorldFog()

        /**
         * Sets whether this BossBar should thicken the fog around the player
         *
         * @param fog whether to thicken the fog
         */
        fun setShouldThickenFog(fog: Boolean) = apply {
            mcValue.setCreateWorldFog(fog)
        }

        override fun toString(): String = "BossBar{name=${getName()}, percent=${getPercent()}, color=${getColor()}, " +
            "style=${getStyle()}, shouldDarkenSky=${shouldDarkenSky()}, " +
            "hasDragonMusic=${hasDragonMusic()}, shouldThickenFog=${shouldThickenFog()}}"
    }

    enum class CTBossBarColor(override val mcValue: BossEvent.BossBarColor) : CTWrapper<BossEvent.BossBarColor> {
        PINK(BossEvent.BossBarColor.PINK),
        BLUE(BossEvent.BossBarColor.BLUE),
        RED(BossEvent.BossBarColor.RED),
        GREEN(BossEvent.BossBarColor.GREEN),
        YELLOW(BossEvent.BossBarColor.YELLOW),
        PURPLE(BossEvent.BossBarColor.PURPLE),
        WHITE(BossEvent.BossBarColor.WHITE),
        ;

        companion object {
            @JvmStatic
            fun fromMC(mcValue: BossEvent.BossBarColor) = entries.first { it.mcValue == mcValue }

            @JvmStatic
            fun from(value: Any) = when (value) {
                is CharSequence -> valueOf(value.toString())
                is BossEvent.BossBarColor -> fromMC(value)
                is CTBossBarColor -> value
                else -> throw IllegalArgumentException("Cannot create BossBars.Color from $value")
            }
        }
    }

    enum class CTBossBarStyle(override val mcValue: BossEvent.BossBarOverlay, val sections: Int) : CTWrapper<BossEvent.BossBarOverlay> {
        ONE(BossEvent.BossBarOverlay.PROGRESS, 1),
        SIX(BossEvent.BossBarOverlay.NOTCHED_6, 6),
        TEN(BossEvent.BossBarOverlay.NOTCHED_10, 10),
        TWELVE(BossEvent.BossBarOverlay.NOTCHED_12, 12),
        TWENTY(BossEvent.BossBarOverlay.NOTCHED_20, 20),
        ;

        companion object {
            @JvmStatic
            fun fromMC(mcValue: BossEvent.BossBarOverlay) = entries.first { it.mcValue == mcValue }

            @JvmStatic
            fun from(value: Any) = when (value) {
                is CharSequence -> valueOf(value.toString())
                is BossEvent.BossBarOverlay -> fromMC(value)
                is CTBossBarStyle -> value
                is Number -> entries.first { it.sections == value }
                else -> throw IllegalArgumentException("Cannot create BossBars.Style from $value")
            }
        }
    }
}
