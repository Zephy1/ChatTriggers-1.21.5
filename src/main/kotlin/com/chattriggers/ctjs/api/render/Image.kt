package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.CTJS
import com.chattriggers.ctjs.api.client.Client
import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.Identifier
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.nio.ByteBuffer
import java.util.UUID
import javax.imageio.ImageIO

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
//#endif

class Image(var image: BufferedImage?) {
    private var texture: Texture? = null
    private var identifier: Identifier? = null

    private val textureWidth = image?.width ?: 0
    private val textureHeight = image?.height ?: 0
    private val aspectRatio = if (textureHeight != 0) textureHeight.toFloat() / textureWidth else 0f

    init {
        CTJS.images.add(this)

        Client.scheduleTask {
            setTexture(bufferedImageToNativeTexture(image!!))
        }
    }

    fun getTextureWidth(): Int = textureWidth

    fun getTextureHeight(): Int = textureHeight

    fun getTexture(): DynamicTexture? = texture?.texture

    fun isReady(): Boolean = texture != null

    fun setTexture(tex: Texture?) {
        texture?.texture?.close()
        texture?.buffer?.let(MemoryUtil::memFree)
        texture = tex

        if (texture == null) return
        if (identifier == null) {
            identifier = Identifier.fromNamespaceAndPath(CTJS.MOD_ID, texture!!.uniqueName)
        }
        Client.getMinecraft().textureManager.register(identifier!!, texture!!.texture)
    }

    internal fun getIdOrRegister(): Identifier {
        if (identifier == null) {
            identifier = Identifier.fromNamespaceAndPath(CTJS.MOD_ID, "image${nextIdentifierIndex++}")
            texture?.let {
                Client.getMinecraft().textureManager.register(identifier!!, it.texture)
            }
        }
        return identifier!!
    }

    /**
     * Clears the image from GPU memory and removes its references CT side
     * that way it can be garbage collected if not referenced in js code.
     */
    fun destroy() {
        if (identifier != null) {
            Client.getMinecraft().textureManager.release(identifier!!)
        }
        texture?.texture?.close()
        texture?.buffer?.let(MemoryUtil::memFree)
        identifier = null
        texture = null
        image = null
    }

    fun getImageSize(
        width: Float? = null,
        height: Float? = null,
    ): Pair<Float, Float> {
        return when {
            width == null && height == null -> textureWidth.toFloat() to textureHeight.toFloat()
            width == null -> height!! / aspectRatio to height
            height == null -> width to width * aspectRatio
            else -> width to height
        }
    }

    @JvmOverloads
    fun drawRGBA(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        zOffset: Float = 0f,
    ) = apply {
        draw(drawContext, xPosition, yPosition, width, height, RenderUtils.RGBAColor(red, green, blue, alpha).getLong(), zOffset)
    }

    @JvmOverloads
    fun draw(
        //#if MC<=12111
        //$$drawContext: GuiGraphics,
        //#else
        drawContext: GuiGraphicsExtractor,
        //#endif
        xPosition: Float,
        yPosition: Float,
        width: Float? = null,
        height: Float? = null,
        color: Long = RenderUtils.WHITE,
        zOffset: Float = 0f,
    ) = apply {
        val (drawWidth, drawHeight) = getImageSize(width, height)
        if (texture == null) return@apply
        GUIRenderer.drawImage(drawContext, this, xPosition, yPosition, drawWidth, drawHeight, color, zOffset)
    }

    data class Texture(val texture: DynamicTexture, val buffer: ByteBuffer, val uniqueName: String)

    companion object {
        private var nextIdentifierIndex = 0

        /**
         * Create an image object from a java.io.File object. Throws an exception
         * if the file cannot be found.
         */
        @JvmStatic
        fun fromFile(file: File): Image {
            val bufferedImage = ImageIO.read(file) ?: throw IllegalArgumentException("Could not read image file.")
            val newImage = Image(bufferedImage)
            return newImage
        }

        /**
         * Create an image object from a file path. Throws an exception
         * if the file cannot be found.
         */
        @JvmStatic
        fun fromFile(file: String) = fromFile(File(file))

        /**
         * Create an image object from a file path, relative to the assets directory.
         * Throws an exception if the file cannot be found.
         */
        @JvmStatic
        fun fromAsset(name: String) = Image(ImageIO.read(File(CTJS.assetsDir, name)))

        /**
         * Creates an image object from a URL. Throws an exception if an image
         * cannot be created from the URL. Will cache the image in the assets
         */
        @JvmStatic
        @JvmOverloads
        fun fromUrl(url: String, cachedImageName: String? = null): Image {
            if (cachedImageName == null) return Image(getImageFromUrl(url))

            val resourceFile = File(CTJS.assetsDir, cachedImageName)

            if (resourceFile.exists()) return Image(ImageIO.read(resourceFile))

            val image = getImageFromUrl(url)
            ImageIO.write(image, "png", resourceFile)
            return Image(image)
        }

        private fun getImageFromUrl(url: String): BufferedImage {
            val req = CTJS.makeWebRequest(url)
            if (req is HttpURLConnection) {
                req.requestMethod = "GET"
                req.doOutput = true
            }

            return ImageIO.read(req.inputStream)
        }

        @JvmStatic
        fun bufferedImageToNativeTexture(image: BufferedImage): Texture {
            return ByteArrayOutputStream().use {
                ImageIO.write(image, "png", it)
                val buffer = MemoryUtil.memAlloc(it.size())
                buffer.put(it.toByteArray())
                buffer.rewind()

                val uniqueName = "image-${UUID.randomUUID()}"
                Texture(
                    DynamicTexture({ "ct:$uniqueName" }, NativeImage.read(buffer)),
                    buffer,
                    uniqueName,
                )
            }
        }
    }
}
