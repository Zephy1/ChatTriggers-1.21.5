package com.chattriggers.ctjs.internal.mixins;

//#if MC>=26.1
import com.chattriggers.ctjs.internal.engine.CTEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {
    @Unique
    private final Map<BlockEntityRenderState, Object[]> ctjs$stateToEntityData = new IdentityHashMap<>();

    @Inject(
        method = "tryExtractRenderState",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;extractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/client/renderer/blockentity/state/BlockEntityRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private <E extends BlockEntity, S extends BlockEntityRenderState> void captureEntityData(
        E blockEntity,
        float partialTicks,
        ModelFeatureRenderer.CrumblingOverlay breakProgress,
        //#if MC>=26.2
        boolean isGloballyRendered,
        //#endif
        CallbackInfoReturnable<S> cir,
        BlockEntityRenderer<E, S> renderer,
        net.minecraft.world.phys.Vec3 cameraPosition,
        S state
    ) {
        ctjs$stateToEntityData.put(state, new Object[]{ blockEntity, partialTicks });
    }

    @Inject(
        method = "submit",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;submit(Lnet/minecraft/client/renderer/blockentity/state/BlockEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private <S extends BlockEntityRenderState> void injectRender(
        S renderState,
        PoseStack matrixStack,
        SubmitNodeCollector queue,
        CameraRenderState cameraRenderState,
        CallbackInfo ci,
        BlockEntityRenderer<?, S> renderer
    ) {
        Object[] data = ctjs$stateToEntityData.remove(renderState);
        if (data != null) {
            BlockEntity entity = (BlockEntity) data[0];
            float partialTicks = (float) data[1];
            CTEvents.RENDER_BLOCK_ENTITY.invoker().render(matrixStack, entity, partialTicks, ci);
        }
    }
}
//#endif
