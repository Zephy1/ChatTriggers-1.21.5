package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.api.message.ChatLib;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//#if MC<=12111
//$$import net.minecraft.client.GuiMessage;
//#else
import net.minecraft.client.multiplayer.chat.GuiMessage;
//#endif

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Final
    @Shadow
    private List<GuiMessage> allMessages;

    @Inject(
        method = "clearMessages",
        at = @At(
            value = "TAIL"
        )
    )
    private void injectClear(boolean clearHistory, CallbackInfo ci) {
        ChatLib.INSTANCE.onChatHudClearChat();
    }

    // TODO: is it this or addVisibleMessage
    @Inject(
        //#if MC<=12111
        //$$method = "addMessageToQueue(Lnet/minecraft/client/GuiMessage;)V",
        //#else
        method = "addMessageToQueue(Lnet/minecraft/client/multiplayer/chat/GuiMessage;)V",
        //#endif
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;removeLast()Ljava/lang/Object;",
            shift = At.Shift.BEFORE
        )
    )
    private void injectMessageRemovedForChatLimit(GuiMessage message, CallbackInfo ci) {
        ChatLib.INSTANCE.onChatHudLineRemoved(allMessages.getLast());
    }

    // Note: ChatHudLine objects are also removed in queueForRemoval, however those are signature based.
    //       The Message objects that CT sends will always create ChatHudLine objects with null signatures,
    //       so objects removed in that method will never be in the ChatLib.chatLineIds map
}
