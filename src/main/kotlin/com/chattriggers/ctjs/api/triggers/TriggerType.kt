package com.chattriggers.ctjs.api.triggers

import com.chattriggers.ctjs.internal.engine.JSLoader

sealed interface ITriggerType {
    val name: String

    fun triggerAll(vararg args: Any?) {
        JSLoader.exec(this, args)
    }
}

enum class TriggerType : ITriggerType {
    // client
    CHAT,
    ACTION_BAR,
    TICK,
    STEP,
    GAME_LOAD,
    GAME_UNLOAD,
    MESSAGE_SENT,
    ITEM_TOOLTIP,
    PLAYER_INTERACT,
    PACKET_SENT,
    PACKET_RECEIVED,
    SERVER_CONNECT,
    SERVER_DISCONNECT,
    DROP_ITEM,

    // gui
    GUI_OPENED,
    GUI_CLOSED,
    CLICKED,
    SCROLLED,
    DRAGGED,
    GUI_KEY,
    GUI_MOUSE_CLICK,
    GUI_MOUSE_DRAG,

    // rendering
    PRE_RENDER_WORLD,
    POST_RENDER_WORLD,
    PRE_RENDER_GUI,
    POST_RENDER_GUI,
    RENDER_BLOCK_HIGHLIGHT,
    RENDER_BLOCK_ENTITY,
    RENDER_ENTITY,
    RENDER_PLAYER_LIST,
    RENDER_HUD_OVERLAY,
    RENDER_SCREEN_OVERLAY,
    RENDER_HIDEABLE_SCREEN_OVERLAY,

    // world
    WORLD_LOAD,
    WORLD_UNLOAD,
    SOUND_PLAY,
    SPAWN_PARTICLE,
    ENTITY_DAMAGE,
    ENTITY_DEATH,

    // misc
    COMMAND,
    OTHER,
	;
}

data class CustomTriggerType(override val name: String) : ITriggerType
