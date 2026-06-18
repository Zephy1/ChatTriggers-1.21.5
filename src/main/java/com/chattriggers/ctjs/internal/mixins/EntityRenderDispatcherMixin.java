package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.internal.engine.CTEvents;
import com.chattriggers.ctjs.api.render.GUIRenderer;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chattriggers.ctjs.api.client.Client;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

//#if MC<=12111
//$$import net.minecraft.client.renderer.state.CameraRenderState;
//#else
import net.minecraft.client.renderer.state.level.CameraRenderState;
//#endif

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(
        method = "onResourceManagerReload",
        at = @At(
            value = "TAIL"
        )
    )
    private void injectReload(ResourceManager manager, CallbackInfo ci, @Local EntityRendererProvider.Context context) {
        GUIRenderer.initializePlayerRenderers(context);
    }

    @Inject(
        method = "submit",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private <S extends EntityRenderState> void injectRender(
        S renderState,
        CameraRenderState cameraRenderState,
        double x,
        double y,
        double z,
        PoseStack matrixStack,
        SubmitNodeCollector orderedRenderCommandQueue,
        CallbackInfo ci
    ) {
//        fixme: this technically works, however i'm unsure whether the targetedEntity is the right entity as code-wise it seems like it's the player/camera entity
//        CTEvents.RENDER_ENTITY.invoker().render(matrixStack, targetedEntity, Client.getMinecraft().getRenderTickCounter().getDynamicDeltaTicks(), ci);
    }
}
