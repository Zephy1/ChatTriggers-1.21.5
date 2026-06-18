package com.chattriggers.ctjs.internal.mixins;

import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

//#if MC<=12111
//$$import net.minecraft.client.GuiMessage;
//#else
import net.minecraft.client.multiplayer.chat.GuiMessage;
//#endif

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor {
    @Accessor
    List<GuiMessage> getAllMessages();

    @Invoker
    void invokeRefreshTrimmedMessages();
}
