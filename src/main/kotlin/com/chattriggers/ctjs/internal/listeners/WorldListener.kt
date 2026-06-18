package com.chattriggers.ctjs.internal.listeners

import com.chattriggers.ctjs.api.render.GUIRenderer
import com.chattriggers.ctjs.api.triggers.CancellableEvent
import com.chattriggers.ctjs.api.triggers.TriggerType
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.core.BlockPos

import com.mojang.blaze3d.vertex.PoseStack
import org.joml.Matrix4f

object WorldListener {
    private var matrixStack: PoseStack? = null
    private var deltaTicks: Float = 1f

    //#if MC>=26.2
    var viewMatrix: Matrix4f = Matrix4f()
        private set
    //#endif

    fun triggerBlockOutline(bp: BlockPos): Boolean {
        val event = CancellableEvent()
        TriggerType.RENDER_BLOCK_HIGHLIGHT.triggerAll(BlockPos(bp), event)
        return event.isCanceled()
    }

    fun setMatrixStack(stack: PoseStack) {
        //#if MC<26.2
        //$$matrixStack = stack
        //#else
        viewMatrix = Matrix4f(RenderSystem.getModelViewStack())
        val copy = PoseStack()
        copy.mulPose(viewMatrix)
        matrixStack = copy
        //#endif
    }

    fun triggerRenderStart(ticks: Float) {
        deltaTicks = ticks
        if (matrixStack == null) return
        GUIRenderer.withMatrix(matrixStack, ticks) {
            TriggerType.PRE_RENDER_WORLD.triggerAll(ticks)
        }
    }

    fun triggerRenderLast() {
        if (matrixStack == null) return
        GUIRenderer.withMatrix(matrixStack, deltaTicks) {
            TriggerType.POST_RENDER_WORLD.triggerAll(deltaTicks)
        }
    }
}
