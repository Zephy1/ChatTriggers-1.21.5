package com.chattriggers.ctjs.api.render

import net.minecraft.resources.Identifier

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
enum class HudRenderLayer(val mcValue: Identifier) {
    MISC_OVERLAYS(VanillaHudElements.MISC_OVERLAYS),
    CROSSHAIR(VanillaHudElements.CROSSHAIR),
    SPECTATOR_MENU(VanillaHudElements.SPECTATOR_MENU),
    HOTBAR(VanillaHudElements.HOTBAR),
    ARMOR_BAR(VanillaHudElements.ARMOR_BAR),
    HEALTH_BAR(VanillaHudElements.HEALTH_BAR),
    FOOD_BAR(VanillaHudElements.FOOD_BAR),
    AIR_BAR(VanillaHudElements.AIR_BAR),
    MOUNT_HEALTH(VanillaHudElements.MOUNT_HEALTH),
    INFO_BAR(VanillaHudElements.INFO_BAR),
    EXPERIENCE_LEVEL(VanillaHudElements.EXPERIENCE_LEVEL),
    HELD_ITEM_TOOLTIP(VanillaHudElements.HELD_ITEM_TOOLTIP),
    SPECTATOR_TOOLTIP(VanillaHudElements.SPECTATOR_TOOLTIP),
    //#if MC<=12111
    //$$STATUS_EFFECTS(VanillaHudElements.STATUS_EFFECTS),
    //#else
    MOB_EFFECTS(VanillaHudElements.MOB_EFFECTS),
    //#endif
    BOSS_BAR(VanillaHudElements.BOSS_BAR),
    SLEEP(VanillaHudElements.SLEEP),
    DEMO_TIMER(VanillaHudElements.DEMO_TIMER),
    SCOREBOARD(VanillaHudElements.SCOREBOARD),
    OVERLAY_MESSAGE(VanillaHudElements.OVERLAY_MESSAGE),
    TITLE_AND_SUBTITLE(VanillaHudElements.TITLE_AND_SUBTITLE),
    CHAT(VanillaHudElements.CHAT),
    PLAYER_LIST(VanillaHudElements.PLAYER_LIST),
    SUBTITLES(VanillaHudElements.SUBTITLES),
    ;
    fun toMC() = mcValue
}
