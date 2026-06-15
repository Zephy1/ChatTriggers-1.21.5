package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.api.world.Scoreboard;
import com.chattriggers.ctjs.api.world.TabList;
import com.chattriggers.ctjs.internal.engine.CTEvents;
import com.chattriggers.ctjs.api.triggers.TriggerType;
import com.chattriggers.ctjs.internal.engine.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC<=12108
//$$import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
//#endif

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public ClientWorld world;

    @Shadow public abstract ServerInfo getCurrentServerEntry();
    @Shadow public abstract boolean isIntegratedServerRunning();

    @Inject(
        method = "joinWorld",
        at = @At(
            value = "HEAD"
        )
    )
    //#if MC<=12108
    //$$private void injectWorldUnload(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
    //#else
    private void injectWorldUnload(ClientWorld world, CallbackInfo ci) {
    //#endif
        if (this.world == null && world != null) {
            TriggerType.SERVER_CONNECT.triggerAll();
        } else if (this.world != null && world == null) {
            TriggerType.SERVER_DISCONNECT.triggerAll();
        }

        if (this.world != null) {
            TriggerType.WORLD_UNLOAD.triggerAll();
            Scoreboard.INSTANCE.clearCustom();
            TabList.INSTANCE.clearCustom();
        }
    }

    @Inject(
        method = "joinWorld",
        at = @At(
            value = "TAIL"
        )
    )
    //#if MC<=12108
    //$$private void injectWorldLoad(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
    //#else
    private void injectWorldLoad(ClientWorld world, CallbackInfo ci) {
    //#endif
        if (world != null) {
            TriggerType.WORLD_LOAD.triggerAll();
        }
    }

    @Inject(
        //#if MC<=12110
        //$$method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V",
        //#else
        method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;ZZ)V",
        //#endif
        at = @At(
            value = "HEAD"
        )
    )
    //#if MC<=12110
    //$$private void injectDisconnect(Screen disconnectionScreen, boolean transferring, CallbackInfo ci) {
    //#else
    private void injectDisconnect(Screen disconnectionScreen, boolean transferring, boolean bl, CallbackInfo ci) {
    //#endif
        // disconnect() is also called when connecting, so we check that there is
        // an existing server
        if (this.isIntegratedServerRunning() || this.getCurrentServerEntry() != null) {
            TriggerType.WORLD_UNLOAD.triggerAll();
            TriggerType.SERVER_DISCONNECT.triggerAll();
            Scoreboard.INSTANCE.clearCustom();
            TabList.INSTANCE.clearCustom();
        }
    }

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
        method = "render",
        at = @At(
            value = "HEAD"
        )
    )
    private void injectRender(boolean tick, CallbackInfo ci) {
        CTEvents.RENDER_GAME.invoker().run();
    }
}
