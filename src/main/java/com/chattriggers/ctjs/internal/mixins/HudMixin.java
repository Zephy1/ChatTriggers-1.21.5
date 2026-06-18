package com.chattriggers.ctjs.internal.mixins;

//#if MC>=26.2
import com.chattriggers.ctjs.api.world.Scoreboard;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HudMixin {
    @Inject(
        method = "displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/scores/Objective;)V",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void injectRenderScoreboard(
        GuiGraphicsExtractor drawContext,
        Objective objective,
        CallbackInfo ci
    ) {
        if (!Scoreboard.getShouldRender()) {
            ci.cancel();
        }
    }
}
//#endif
