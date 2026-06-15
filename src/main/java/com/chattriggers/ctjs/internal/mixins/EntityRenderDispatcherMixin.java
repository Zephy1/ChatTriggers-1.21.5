package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.internal.engine.CTEvents;
import com.chattriggers.ctjs.api.render.GUIRenderer;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC<=12108
//$$import net.minecraft.client.render.VertexConsumerProvider;
//$$import net.minecraft.client.render.entity.EntityRenderDispatcher;
//$$import net.minecraft.entity.Entity;
//#else
import com.chattriggers.ctjs.api.client.Client;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
//#endif

//#if MC<=12108
//$$@Mixin(EntityRenderDispatcher.class)
//#else
@Mixin(EntityRenderManager.class)
//#endif
public abstract class EntityRenderDispatcherMixin {
    @Inject(
        method = "reload",
        at = @At(
            value = "TAIL"
        )
    )
    private void injectReload(ResourceManager manager, CallbackInfo ci, @Local EntityRendererFactory.Context context) {
        GUIRenderer.initializePlayerRenderers(context);
    }

    @Inject(
        //#if MC<=12108
        //$$method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        //#else
        method = "render",
        //#endif
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    //#if MC<=12108
    //$$private void injectRender(
    //$$    Entity entity,
    //$$    double x,
    //$$    double y,
    //$$    double z,
    //$$    float tickDelta,
    //$$    MatrixStack matrixStack,
    //$$    VertexConsumerProvider vertexConsumers,
    //$$    int light,
    //$$    CallbackInfo ci
    //$$) {
    //$$    CTEvents.RENDER_ENTITY.invoker().render(matrixStack, entity, tickDelta, ci);
    //#else
    private <S extends EntityRenderState> void injectRender(
        S renderState,
        CameraRenderState cameraRenderState,
        double x,
        double y,
        double z,
        MatrixStack matrixStack,
        OrderedRenderCommandQueue orderedRenderCommandQueue,
        CallbackInfo ci
    ) {
//        fixme: this technically works, however i'm unsure whether the targetedEntity is the right entity as code-wise it seems like it's the player/camera entity
//        CTEvents.RENDER_ENTITY.invoker().render(matrixStack, targetedEntity, Client.getMinecraft().getRenderTickCounter().getDynamicDeltaTicks(), ci);
    //#endif
    }
}
