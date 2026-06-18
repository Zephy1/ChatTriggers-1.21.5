package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.api.triggers.TriggerType;
import com.chattriggers.ctjs.api.world.TabList;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics;
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor;
//#endif

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
    @Inject(
        //#if MC<=12111
        //$$method = "render",
        //#else
        method = "extractRenderState",
        //#endif
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void injectRenderPlayerList(
        //#if MC<=12111
        //$$GuiGraphics drawContext,
        //#else
        GuiGraphicsExtractor drawContext,
        //#endif
        int scaledWindowWidth, Scoreboard scoreboard, Objective objective, CallbackInfo ci
    ) {
        TriggerType.RENDER_PLAYER_LIST.triggerAll(ci);
    }

    @Inject(
        method = "setHeader",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void ctjs$keepCustomHeader(Component header, CallbackInfo ci) {
        if (TabList.INSTANCE.getCustomHeader()) {
            ci.cancel();
        }
    }

    @Inject(
        method = "setFooter",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void ctjs$keepCustomFooter(Component footer, CallbackInfo ci) {
        if (TabList.INSTANCE.getCustomFooter()) {
            ci.cancel();
        }
    }
}
