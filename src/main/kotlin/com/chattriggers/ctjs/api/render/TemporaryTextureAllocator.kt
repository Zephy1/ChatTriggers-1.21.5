package com.chattriggers.ctjs.api.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.GpuTexture

//#if MC<26.2
//$$import com.mojang.blaze3d.textures.TextureFormat
//#else
import com.mojang.blaze3d.GpuFormat
//#endif

/**
 * Allocates temporary textures, which are valid for one frame, from a pool.
 */
class TemporaryTextureAllocator(
    private val allCleanedUp: () -> Unit = {},
) : AutoCloseable {
    // When we allocate a texture, we need to hold on to it until the next frame so MC's gui renderer can use it
    private val usedAllocations = mutableListOf<TextureAllocation>()
    // We hold on to it for an additionally frame so we can re-use it instead of having to re-allocate one each frame
    private val reusableAllocations = mutableListOf<TextureAllocation>()

    fun allocate(width: Int, height: Int): TextureAllocation {
        var texture = reusableAllocations.removeLastOrNull()

        if (texture != null && (texture.width != width || texture.height != height)) {
            texture.close()
            texture = null
        }

        if (texture == null) {
            texture = TextureAllocation(width, height)
        }

        val device = RenderSystem.getDevice()
        //#if MC<26.2
        //$$val clearColor = 0
        //#else
        val clearColor = org.joml.Vector4f(0f)
        //#endif
        device.createCommandEncoder().clearColorAndDepthTextures(texture.texture, clearColor, texture.depthTexture, 1.0)

        usedAllocations.add(texture)

        return texture
    }

    fun free(allocation: TextureAllocation) {
        if (usedAllocations.remove(allocation)) {
            reusableAllocations.add(allocation)
        }
    }

    fun nextFrame() {
        reusableAllocations.forEach { it.close() }
        reusableAllocations.clear()
        reusableAllocations.addAll(usedAllocations)
        usedAllocations.clear()

        if (reusableAllocations.isEmpty()) {
            allCleanedUp()
        }
    }

    override fun close() {
        nextFrame()
        nextFrame()
        assert(usedAllocations.isEmpty())
        assert(reusableAllocations.isEmpty())
    }

    class TextureAllocation(
        val width: Int,
        val height: Int,
    ) : AutoCloseable {
        private val gpuDevice = RenderSystem.getDevice()

        var texture = gpuDevice.createTexture(
            { "Pre-rendered texture" },
            GpuTexture.USAGE_COPY_DST or GpuTexture.USAGE_COPY_SRC or GpuTexture.USAGE_RENDER_ATTACHMENT or GpuTexture.USAGE_TEXTURE_BINDING,
            //#if MC<26.2
            //$$TextureFormat.RGBA8,
            //#else
            GpuFormat.RGBA8_UNORM,
            //#endif
            width,
            height,
            1,
            1
        ).apply { }
        var depthTexture = gpuDevice.createTexture(
            { "Pre-rendered depth texture" },
            GpuTexture.USAGE_COPY_DST or GpuTexture.USAGE_COPY_SRC or GpuTexture.USAGE_RENDER_ATTACHMENT,
            //#if MC<26.2
            //$$TextureFormat.DEPTH32,
            //#else
            GpuFormat.D32_FLOAT,
            //#endif
            width,
            height,
            1,
            1
        )

        var textureView = gpuDevice.createTextureView(texture)
        var depthTextureView = gpuDevice.createTextureView(depthTexture)

        override fun close() {
            depthTextureView.close()
            textureView.close()
            depthTexture.close()
            texture.close()
        }
    }
}
