package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.internal.listeners.WorldListener;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;

import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC<=12111
//$$import net.minecraft.client.renderer.state.LevelRenderState;
//$$import net.minecraft.client.renderer.state.BlockOutlineRenderState;
//$$import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//$$import net.minecraft.client.renderer.state.CameraRenderState;
//#else
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
//#endif

//#if MC>=26.1
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
//#endif

//#if MC<26.2
//$$import net.minecraft.util.profiling.ProfilerFiller;
//$$import org.joml.Matrix4f;
//$$import com.mojang.blaze3d.resource.ResourceHandle;
//$$import net.minecraft.client.Camera;
//$$import net.minecraft.client.renderer.MultiBufferSource;
//#endif

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Inject(
        //#if MC<26.2
        //$$method = "renderBlockOutline",
        //$$at = @At(
        //$$    value = "FIELD",
        //#if MC<=12111
        //$$    target = "Lnet/minecraft/client/renderer/state/CameraRenderState;pos:Lnet/minecraft/world/phys/Vec3;"
        //#else
        //$$    target = "Lnet/minecraft/client/renderer/state/level/CameraRenderState;pos:Lnet/minecraft/world/phys/Vec3;"
        //#endif
        //$$),
        //#else
        method = "submitBlockOutline",
        at = @At("HEAD"),
        //#endif
        cancellable = true
        //#if MC<=12111
        //$$,locals = LocalCapture.CAPTURE_FAILSOFT
        //#endif
    )
    private void onDrawBlockOutline(
        //#if MC<26.2
        //$$MultiBufferSource.BufferSource immediate,
        //#endif
        PoseStack matrices,
        //#if MC<26.2
        //$$boolean renderBlockOutline,
        //#else
        SubmitNodeCollector submitNodeCollector,
        //#endif
        LevelRenderState levelRenderState,
        CallbackInfo ci
        //#if MC<=12111
        //$$,BlockOutlineRenderState outlineRenderState
        //#endif
    ) {
        //#if MC<=12111
        //$$if (WorldListener.INSTANCE.triggerBlockOutline(outlineRenderState.pos())) {
        //#else
        if (levelRenderState.blockOutlineRenderState == null) {
            return;
        }
        if (WorldListener.INSTANCE.triggerBlockOutline(levelRenderState.blockOutlineRenderState.pos())) {
            //#endif
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
        //#if MC<=12111
        //$$method = "method_62214",
        //#elseif MC<26.2
        //$$method = "lambda$addMainPass$0",
        //#else
        method = "submitFeatures",
        //#endif
        at = @At(
            value = "NEW",
            target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"
        )
    )
    private PoseStack onMatrixStack(PoseStack original) {
        WorldListener.INSTANCE.setMatrixStack(original);
        return original;
    }

    @Inject(
        //#if MC<26.2
        //$$method = "renderLevel",
        //#else
        method = "render",
        //#endif
        at = @At("HEAD")
    )
    private void beforeRender(
        GraphicsResourceAllocator allocator,
        DeltaTracker tickCounter,
        boolean renderBlockOutline,
        //#if MC<=12111
        //$$Camera camera,
        //$$Matrix4f positionMatrix,
        //$$Matrix4f matrix4f,
        //$$Matrix4f projectionMatrix,
        //#else
        CameraRenderState cameraState,
        Matrix4fc modelViewMatrix,
        //#endif
        GpuBufferSlice fogBuffer,
        Vector4f fogColor,
        boolean renderSky,
        //#if MC>=26.1 && MC<26.2
        //$$ChunkSectionsToRender chunkSectionsToRender,
        //#endif
        CallbackInfo ci
    ) {
        WorldListener.INSTANCE.triggerRenderStart(tickCounter.getGameTimeDeltaTicks());
    }

    @Inject(
        //#if MC<=12111
        //$$method = "method_62214",
        //#elseif MC<26.2
        //$$method = "lambda$addMainPass$0",
        //#else
        method = "render",
        //#endif
        at = @At("RETURN")
    )
    private void afterRender(
        //#if MC<26.2
        //$$GpuBufferSlice gpuBufferSlice,
        //$$LevelRenderState worldRenderState,
        //$$ProfilerFiller profiler,
        //#if MC>=26.1 && MC<26.2
        //$$ChunkSectionsToRender chunkSectionsToRender,
        //$$ResourceHandle entityOutlineTarget,
        //$$ResourceHandle translucentTarget,
        //$$ResourceHandle mainTarget,
        //$$ResourceHandle itemEntityTarget,
        //$$ResourceHandle particleTarget,
        //$$boolean renderOutline,
        //$$Matrix4fc modelViewMatrix,
        //#else
        //$$Matrix4f matrix4f,
        //$$ResourceHandle handle,
        //$$ResourceHandle handle2,
        //$$boolean bl,
        //$$ResourceHandle handle3,
        //$$ResourceHandle handle4,
        //#endif
        //#else
        GraphicsResourceAllocator resourceAllocator,
        DeltaTracker deltaTracker,
        boolean renderOutline,
        CameraRenderState cameraState,
        Matrix4fc modelViewMatrix,
        GpuBufferSlice terrainFog,
        Vector4f fogColor,
        boolean shouldRenderSky,
        //#endif
        CallbackInfo ci
    ) {
        WorldListener.INSTANCE.triggerRenderLast();
    }
}
