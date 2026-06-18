package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.api.world.Scoreboard;
import com.chattriggers.ctjs.api.world.TabList;
import com.chattriggers.ctjs.internal.engine.CTEvents;
import com.chattriggers.ctjs.api.triggers.TriggerType;
import com.chattriggers.ctjs.internal.engine.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public ClientLevel level;

    @Shadow public abstract ServerData getCurrentServer();
    @Shadow public abstract boolean hasSingleplayerServer();

    @Inject(
        method = "setLevel",
        at = @At(
            value = "HEAD"
        )
    )
    private void injectWorldUnload(ClientLevel world, CallbackInfo ci) {
        if (this.level == null && world != null) {
            TriggerType.SERVER_CONNECT.triggerAll();
        } else if (this.level != null && world == null) {
            TriggerType.SERVER_DISCONNECT.triggerAll();
        }

        if (this.level != null) {
            TriggerType.WORLD_UNLOAD.triggerAll();
            Scoreboard.INSTANCE.clearCustom();
            TabList.INSTANCE.clearCustom();
        }
    }

    @Inject(
        method = "setLevel",
        at = @At(
            value = "TAIL"
        )
    )
    private void injectWorldLoad(ClientLevel world, CallbackInfo ci) {
        if (world != null) {
            TriggerType.WORLD_LOAD.triggerAll();
        }
    }

    @Inject(
        method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V",
        at = @At(
            value = "HEAD"
        )
    )
    private void injectDisconnect(Screen disconnectionScreen, boolean transferring, boolean bl, CallbackInfo ci) {
        // disconnect() is also called when connecting, so we check that there is
        // an existing server
        if (this.hasSingleplayerServer() || this.getCurrentServer() != null) {
            TriggerType.WORLD_UNLOAD.triggerAll();
            TriggerType.SERVER_DISCONNECT.triggerAll();
            Scoreboard.INSTANCE.clearCustom();
            TabList.INSTANCE.clearCustom();
        }
    }

    //#if MC<26.2
    //$$@Inject(
    //$$    method = "setScreen",
    //$$    at = @At(
    //$$        value = "HEAD"
    //$$    )
    //$$)
    //$$private void injectScreenOpened(Screen screen, CallbackInfo ci) {
    //$$    if (screen != null) {
    //$$        TriggerType.GUI_OPENED.triggerAll(screen, ci);
    //$$    }
    //$$}
    //#endif

    @Inject(
        method = "run",
        at = @At(
            value = "HEAD"
        )
    )
    private void injectRun(CallbackInfo ci) {
        new Thread(() -> {
            ModuleManager.INSTANCE.entryPass();
            TriggerType.GAME_LOAD.triggerAll();
        }).start();
    }

    @Inject(
        method = "runTick",
        at = @At(
            value = "HEAD"
        )
    )
    private void injectRender(boolean tick, CallbackInfo ci) {
        CTEvents.RENDER_GAME.invoker().run();
    }
}
