package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.api.client.Client
import com.mojang.blaze3d.ProjectionType
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.Identifier

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics
//$$import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.Projection
import net.minecraft.client.renderer.ProjectionMatrixBuffer
//#endif

/**
 * Allows rendering of raw OpenGL into [DrawContext] by drawing to a temporary texture which is then submitted as a
 * plain textured quad to [DrawContext].
 *
 * You **MUST** call [nextFrame] before the next frame begins but no sooner than MC's GuiRenderer actually using the
 * submitted textures. Repeated calls to [drawImmediate] without [nextFrame] or [close] inbetween will keep allocating
 * more and more gpu memory!
 * If you cannot guarantee further calls to [nextFrame], you must call [close] to release all resources.
 * The [AdvancedDrawContext] remains usable, [close] merely frees all resources, further calls to [drawImmediate] will
 * simply re-allocate them.
 */
class AdvancedDrawContext : AutoCloseable {
    //#if MC<=12111
    //$$private var allocatedProjectionMatrix: CachedOrthoProjectionMatrixBuffer? = null
    //#else
    private val projection = Projection()
    private var allocatedProjectionMatrix: ProjectionMatrixBuffer? = null
    //#endif

    private val textureAllocator = TemporaryTextureAllocator {
        allocatedProjectionMatrix?.close()
        allocatedProjectionMatrix = null
    }

    //#if MC<=12111
    //$$fun drawImmediate(context: GuiGraphics, block: (UMatrixStack) -> Unit) {
    //#else
    fun drawImmediate(context: GuiGraphicsExtractor, block: (UMatrixStack) -> Unit) {
    //#endif
        val scaleFactor = Client.getMinecraft().window.guiScale
        val width = Client.getMinecraft().window.width
        val height = Client.getMinecraft().window.height

        val texture = textureAllocator.allocate(width, height)

        var projectionMatrix = allocatedProjectionMatrix
        if (projectionMatrix == null) {
            //#if MC<=12111
            //$$projectionMatrix = CachedOrthoProjectionMatrixBuffer("pre-rendered screen", 1000f, 21000f, true)
            //#else
            projectionMatrix = ProjectionMatrixBuffer("pre-rendered screen")
            //#endif
            allocatedProjectionMatrix = projectionMatrix
        }
        //#if MC<=12111
        //$$val projectionMatrixBuffer = projectionMatrix.getBuffer(width.toFloat() / scaleFactor, height.toFloat() / scaleFactor)
        //#else
        projection.setupOrtho(1000f, 21000f, width.toFloat() / scaleFactor, height.toFloat() / scaleFactor, true)
        val projectionMatrixBuffer = projectionMatrix.getBuffer(projection)
        //#endif
        RenderSystem.setProjectionMatrix(projectionMatrixBuffer, ProjectionType.ORTHOGRAPHIC)

        val orgOutputColorTextureOverride = RenderSystem.outputColorTextureOverride
        val orgOutputDepthTextureOverride = RenderSystem.outputDepthTextureOverride
        RenderSystem.outputColorTextureOverride = texture.textureView
        RenderSystem.outputDepthTextureOverride = texture.depthTextureView

        val matrixStack = UMatrixStack()
        matrixStack.translate(0f, 0f, -10000f)
        block(matrixStack)

        RenderSystem.outputColorTextureOverride = orgOutputColorTextureOverride
        RenderSystem.outputDepthTextureOverride = orgOutputDepthTextureOverride

        draw(context, texture)
    }

    //#if MC<=12111
    //$$fun draw(context: GuiGraphics, texture: TemporaryTextureAllocator.TextureAllocation) {
    //#else
    fun draw(context: GuiGraphicsExtractor, texture: TemporaryTextureAllocator.TextureAllocation) {
    //#endif
        val width = texture.width
        val height = texture.height
        val scaleFactor = Client.getMinecraft().window.guiScale.toFloat()

        val textureManager = Client.getMinecraft().textureManager
        val identifier = Identifier.fromNamespaceAndPath("ct", "__tmp_texture__")
        textureManager.register(identifier, object : AbstractTexture() {
            init { textureView = texture.textureView }
            override fun close() {} // we don't want the later `destroyTexture` to close our texture
        })

        context.pose().pushMatrix()
        context.pose().scale(1 / scaleFactor, 1 / scaleFactor) // drawTexture only accepts `int`s
        context.blit(
            RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
            identifier,
            // x, y
            0, 0,
            // u, v
            0f, height.toFloat(),
            // width, height
            width, height,
            // uWidth, vHeight
            width, -height,
            // textureWidth, textureHeight
            width, height,
        )
        context.pose().popMatrix()

        textureManager.release(identifier)
    }

    fun nextFrame() {
        textureAllocator.nextFrame()
    }

    override fun close() {
        textureAllocator.close()
    }
}
