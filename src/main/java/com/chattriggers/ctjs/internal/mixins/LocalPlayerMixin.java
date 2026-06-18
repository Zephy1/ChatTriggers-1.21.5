package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.api.client.CTPlayer;
import com.chattriggers.ctjs.api.inventory.CTItem;
import com.chattriggers.ctjs.api.triggers.TriggerType;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Inject(
        method = "drop",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void injectDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        // dropping item while not in gui
        CTItem stack = CTPlayer.getHeldItem();
        if (stack != null && !stack.getMcValue().isEmpty()) {
            TriggerType.DROP_ITEM.triggerAll(stack, entireStack, cir);
        }
    }
}
