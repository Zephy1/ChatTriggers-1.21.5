package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.internal.utils.getOption
import net.minecraft.network.chat.Style
import org.mozilla.javascript.NativeObject

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
//#endif

class Text {
    private lateinit var string: String
    private var x: Int = 0
    private var y: Int = 0

    private val lines = mutableListOf<String>()

    private var color = 0xFFFFFFFF
    private var backgroundColor = 0xFF000000
    private var formatted = true
    private var shadow = false
    private var align = Align.LEFT
    private var background = false

    private var width = 0
    private var maxWidth = 0
    private var maxLines = Int.MAX_VALUE
    private var scale = 1f

    @JvmOverloads
    constructor(string: String, x: Int = 0, y: Int = 0) {
        setString(string)
        setX(x)
        setY(y)
    }

    constructor(string: String, config: NativeObject) {
        setString(string)
        setColor(config.getOption("color", 0xFFFFFFFF))
        setFormatted(config.getOption("formatted", true))
        setShadow(config.getOption("shadow", false))
        setAlign(config.getOption("align", Align.LEFT))
        setBackground(config.getOption("background", false))
        setBackgroundColor(config.getOption("backgroundColor", 0x00000000))
        setX(config.getOption("x", 0))
        setY(config.getOption("y", 0))
        setMaxLines((config.getOption("maxLines", Int.MAX_VALUE)))
        setScale(config.getOption("scale", 1f))
        setMaxWidth(config.getOption("maxWidth", 0))
    }

    fun getString(): String = string

    fun setString(string: String) = apply {
        this.string = string
        updateFormatting()
    }

    fun getColor(): Long = color

    fun setColor(color: Long) = apply {
        this.color = RenderUtils.fixAlpha(color)
    }

    fun getFormatted(): Boolean = formatted

    fun setFormatted(formatted: Boolean) = apply {
        this.formatted = formatted
        updateFormatting()
    }

    fun getShadow(): Boolean = shadow

    fun setShadow(shadow: Boolean) = apply {
        this.shadow = shadow
    }

    fun getAlign(): Align = align

    fun setAlign(align: Any) = apply {
        this.align = when (align) {
            is CharSequence -> Align.valueOf(align.toString().uppercase())
            is Align -> align
            else -> Align.LEFT
        }
    }

    fun getBackground(): Boolean = background

    /**
     * Set the background
     *
     * true: Background is enabled
     * false: Background is disabled
     */
    fun setBackground(background: Boolean) = apply {
        this.background = background
    }

    fun getBackgroundColor(): Long = backgroundColor

    fun setBackgroundColor(backgroundColor: Long) = apply {
        this.backgroundColor = backgroundColor
    }

    fun getX(): Int = x

    fun setX(x: Int) = apply {
        this.x = x
    }

    fun getY(): Int = y

    fun setY(y: Int) = apply {
        this.y = y
    }

    /**
     * Gets the width of the text
     * This is automatically updated when the text is drawn.
     *
     * @return the width of the text
     */
    fun getWidth(): Int = width

    fun getLines(): List<String> = lines

    fun getMaxLines(): Int = maxLines

    fun setMaxLines(maxLines: Int) = apply {
        this.maxLines = maxLines
    }

    fun getScale(): Float = scale

    fun setScale(scale: Float) = apply {
        this.scale = scale
    }

    /**
     * Sets the maximum width of the text, splitting it into multiple lines if necessary.
     *
     * @param maxWidth the maximum width of the text
     * @return the Text object for method chaining
     */
    fun setMaxWidth(maxWidth: Int) = apply {
        this.maxWidth = maxWidth
        updateFormatting()
    }

    fun getMaxWidth(): Int = maxWidth

    fun getHeight(): Int {
        return if (lines.size > 1) {
            lines.size.coerceAtMost(maxLines) * 10
        } else {
            10
        }
    }

    fun exceedsMaxLines(): Boolean {
        return lines.size > maxLines
    }

    @JvmOverloads
    fun draw(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        x: Int? = null,
        y: Int? = null
    ) = apply {
        draw(drawContext, x, y, null, null)
    }

    internal fun draw(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        x: Int? = null,
        y: Int? = null,
        backgroundX: Int? = null,
        backgroundWidth: Int? = null
    ) = apply {
        drawContext.pose().pushMatrix()
        drawContext.pose().scale(scale, scale)

        var longestLine = lines.maxOf { RenderUtils.getStringWidth(it) * scale }
        if (maxWidth != 0) {
            longestLine = longestLine.coerceAtMost(maxWidth.toFloat())
        }
        width = longestLine.toInt()

        var yHolder = y ?: this.y
        val xHolder = when (align) {
            Align.CENTER -> (x ?: this.x) - width / 2
            Align.RIGHT -> (x ?: this.x) - width
            else -> x ?: this.x
        }

        if (background) {
            val ox = backgroundX ?: xHolder
            drawContext.fill(
                ox,
                yHolder,
                ox + (backgroundWidth ?: width),
                yHolder + getHeight(),
                backgroundColor.toInt(),
            )
        }

        for (i in 0 until maxLines) {
            if (i >= lines.size) break
            //#if MC<=12111
            //$$drawContext.drawString(
            //#else
            drawContext.text(
            //#endif
                RenderUtils.getTextRenderer(),
                lines[i],
                xHolder,
                yHolder,
                color.toInt(),
                shadow,
            )
            yHolder += 10
        }
        drawContext.pose().popMatrix()
    }

    private fun updateFormatting() {
        string =
            if (formatted) {
                ChatLib.addColor(string)
            } else {
                ChatLib.replaceFormatting(string)
            }

        lines.clear()

        string.split("\n").forEach { line ->
            if (maxWidth > 0) {
                lines.addAll(
                    RenderUtils
                        .getTextRenderer()
                        .splitter
                        .splitLines(line, maxWidth, Style.EMPTY)
                        .map { it.string },
                )
            } else {
                lines.add(line)
            }
        }
    }

    override fun toString() =
        "Text{" +
            "string=$string, x=$x, y=$y, " +
            "lines=$lines, color=$color, scale=$scale, " +
            "formatted=$formatted, shadow=$shadow,  + align=$align, " +
            "width=$width, maxWidth=$maxWidth, maxLines=$maxLines" +
        "}"

    enum class Align {
        LEFT,
        CENTER,
        RIGHT,
		;
    }
}
