package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.api.inventory.CTItem;
import com.chattriggers.ctjs.api.message.TextComponent;
import com.chattriggers.ctjs.api.triggers.TriggerType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics;
//$$import net.minecraft.world.inventory.ClickType;
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.inventory.ContainerInput;
//#endif

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin extends Screen {
    @Shadow
    protected Slot hoveredSlot;

    @Shadow
    @Final
    protected AbstractContainerMenu menu;

    private AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(
        //#if MC<=12111
        //$$method = "renderTooltip",
        //#else
        method = "extractTooltip",
        //#endif
        at = @At(
            value = "INVOKE",
            //#if MC<=12111
            //$$target = "Lnet/minecraft/client/gui/GuiGraphics;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V"
            //#else
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V"
            //#endif
        ),
        cancellable = true
    )
    private void injectDrawMouseoverTooltip(
        //#if MC<=12111
        //$$GuiGraphics drawContext,
        //#else
        GuiGraphicsExtractor drawContext,
        //#endif
        int x, int y, CallbackInfo ci
    ) {
        ItemStack stack = hoveredSlot.getItem();
        TriggerType.ITEM_TOOLTIP.triggerAll(
            getTooltipFromItem(Objects.requireNonNull(minecraft), stack)
                .stream()
                .map(TextComponent::new)
                .toList(),
            CTItem.fromMC(stack),
            ci
        );
    }

    @Inject(
        //#if MC<=12111
        //$$method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V",
        //#else
        method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ContainerInput;)V",
        //#endif
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    private void injectOnMouseClick(
        Slot slot, int slotId, int button,
        //#if MC<=12111
        //$$ClickType actionType,
        //#else
        ContainerInput actionType,
        //#endif
        CallbackInfo ci
    ) {
        if (
            //#if MC<=12111
            //$$(slotId != -999 && actionType == ClickType.THROW) || // dropping item from slot
            //$$(slotId == -999 && actionType == ClickType.PICKUP) // dropping by clicking outside inventory
            //#else
            (slotId != -999 && actionType == ContainerInput.THROW) || // dropping item from slot
            (slotId == -999 && actionType == ContainerInput.PICKUP) // dropping by clicking outside inventory
            //#endif
        ) {
            TriggerType.DROP_ITEM.triggerAll(CTItem.fromMC(menu.getCarried()), button == 0, ci);
        }
    }
}
