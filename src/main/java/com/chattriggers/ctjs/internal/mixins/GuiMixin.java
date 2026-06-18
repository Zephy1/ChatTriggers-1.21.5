package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.api.world.Scoreboard;
import com.chattriggers.ctjs.internal.engine.CTEvents;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.scores.Objective;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics;
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor;
//#endif

//#if MC>=26.2
import net.minecraft.client.gui.screens.Screen;
import com.chattriggers.ctjs.api.triggers.TriggerType;
import com.llamalad7.mixinextras.sugar.Local;
//#endif

@Mixin(Gui.class)
public class GuiMixin {
    //#if MC<26.2
    //$$@Inject(
        //#if MC<=12111
        //$$method = "displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/scores/Objective;)V",
        //#else
        //$$method = "displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/scores/Objective;)V",
        //#endif
    //$$    at = @At(
    //$$        value = "HEAD"
    //$$    ),
    //$$    cancellable = true
    //$$)
    //$$private void injectRenderScoreboard(
        //#if MC<=12111
        //$$GuiGraphics drawContext,
        //#else
        //$$GuiGraphicsExtractor drawContext,
        //#endif
    //$$    Objective objective, CallbackInfo ci
    //$$) {
    //$$    if (!Scoreboard.getShouldRender()) {
    //$$        ci.cancel();
    //$$    }
    //$$}
    //#endif

    @Inject(
        //#if MC<=12111
        //$$method = "render",
        //#else
        method = "extractRenderState",
        //#endif
		at = @At(
            value = "TAIL"
		)
    )
    private void injectRenderOverlay(
        //#if MC<=12111
        //$$GuiGraphics drawContext,
        //#elseif MC<26.2
        //$$GuiGraphicsExtractor drawContext,
        //#endif
        DeltaTracker tickCounter,
        //#if MC>=26.2
        boolean shouldRenderLevel,
        boolean resourcesLoaded,
        //#endif
        CallbackInfo ci
        //#if MC>=26.2
        ,@Local GuiGraphicsExtractor drawContext
        //#endif
    ) {
        CTEvents.RENDER_HUD_OVERLAY.invoker().render(drawContext, new UMatrixStack(drawContext.pose()).toMC(), tickCounter.getGameTimeDeltaTicks());
    }

    //#if MC>=26.2
    @Inject(
        method = "setScreen",
        at = @At(
            value = "HEAD"
        )
    )
    private void injectScreenOpened(Screen screen, CallbackInfo ci) {
        if (screen != null) {
            TriggerType.GUI_OPENED.triggerAll(screen, ci);
        }
    }
    //#endif
}
