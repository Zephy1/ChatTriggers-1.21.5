package com.chattriggers.ctjs.api.client

import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

data class KeyData(
    val glfwKeycode: Int,
    val prettyName: String,
)

data class KeyModifiers(
    val ctrl: Boolean = false,
    val shift: Boolean = false,
    val alt: Boolean = false,
)
fun KeyModifiers?.toInt() = listOf(
    this?.ctrl to GLFW.GLFW_MOD_CONTROL,
    this?.shift to GLFW.GLFW_MOD_SHIFT,
    this?.alt to GLFW.GLFW_MOD_ALT,
).sumOf { (modifier, value) -> if (modifier == true) value else 0 }
fun Int.toModifiers() = KeyModifiers(
    ctrl = (this and GLFW.GLFW_MOD_CONTROL) != 0,
    shift = (this and GLFW.GLFW_MOD_SHIFT) != 0,
    alt = (this and GLFW.GLFW_MOD_ALT) != 0,
)

object Keyboard {
    val keyNameToKeyData: Map<String, KeyData> = mapOf(
        "KEY_UNKNOWN"      to KeyData(GLFW.GLFW_KEY_UNKNOWN,       "Unknown"),
        "KEY_NONE"         to KeyData(GLFW.GLFW_KEY_UNKNOWN,       "None"),
        "KEY_ESCAPE"       to KeyData(GLFW.GLFW_KEY_ESCAPE,        "Escape"),
        "KEY_1"            to KeyData(GLFW.GLFW_KEY_1,             "1"),
        "KEY_2"            to KeyData(GLFW.GLFW_KEY_2,             "2"),
        "KEY_3"            to KeyData(GLFW.GLFW_KEY_3,             "3"),
        "KEY_4"            to KeyData(GLFW.GLFW_KEY_4,             "4"),
        "KEY_5"            to KeyData(GLFW.GLFW_KEY_5,             "5"),
        "KEY_6"            to KeyData(GLFW.GLFW_KEY_6,             "6"),
        "KEY_7"            to KeyData(GLFW.GLFW_KEY_7,             "7"),
        "KEY_8"            to KeyData(GLFW.GLFW_KEY_8,             "8"),
        "KEY_9"            to KeyData(GLFW.GLFW_KEY_9,             "9"),
        "KEY_0"            to KeyData(GLFW.GLFW_KEY_0,             "0"),
        "KEY_MINUS"        to KeyData(GLFW.GLFW_KEY_MINUS,         "-"),
        "KEY_EQUALS"       to KeyData(GLFW.GLFW_KEY_EQUAL,         "="),
        "KEY_BACKSPACE"    to KeyData(GLFW.GLFW_KEY_BACKSPACE,     "Backspace"),
        "KEY_TAB"          to KeyData(GLFW.GLFW_KEY_TAB,           "Tab"),
        "KEY_Q"            to KeyData(GLFW.GLFW_KEY_Q,             "Q"),
        "KEY_W"            to KeyData(GLFW.GLFW_KEY_W,             "W"),
        "KEY_E"            to KeyData(GLFW.GLFW_KEY_E,             "E"),
        "KEY_R"            to KeyData(GLFW.GLFW_KEY_R,             "R"),
        "KEY_T"            to KeyData(GLFW.GLFW_KEY_T,             "T"),
        "KEY_Y"            to KeyData(GLFW.GLFW_KEY_Y,             "Y"),
        "KEY_U"            to KeyData(GLFW.GLFW_KEY_U,             "U"),
        "KEY_I"            to KeyData(GLFW.GLFW_KEY_I,             "I"),
        "KEY_O"            to KeyData(GLFW.GLFW_KEY_O,             "O"),
        "KEY_P"            to KeyData(GLFW.GLFW_KEY_P,             "P"),
        "KEY_LBRACKET"     to KeyData(GLFW.GLFW_KEY_LEFT_BRACKET,  "["),
        "KEY_RBRACKET"     to KeyData(GLFW.GLFW_KEY_RIGHT_BRACKET, "]"),
        "KEY_ENTER"        to KeyData(GLFW.GLFW_KEY_ENTER,         "Enter"),
        "KEY_LCONTROL"     to KeyData(GLFW.GLFW_KEY_LEFT_CONTROL,  "L-Control"),
        "KEY_A"            to KeyData(GLFW.GLFW_KEY_A,             "A"),
        "KEY_S"            to KeyData(GLFW.GLFW_KEY_S,             "S"),
        "KEY_D"            to KeyData(GLFW.GLFW_KEY_D,             "D"),
        "KEY_F"            to KeyData(GLFW.GLFW_KEY_F,             "F"),
        "KEY_G"            to KeyData(GLFW.GLFW_KEY_G,             "G"),
        "KEY_H"            to KeyData(GLFW.GLFW_KEY_H,             "H"),
        "KEY_J"            to KeyData(GLFW.GLFW_KEY_J,             "J"),
        "KEY_K"            to KeyData(GLFW.GLFW_KEY_K,             "K"),
        "KEY_L"            to KeyData(GLFW.GLFW_KEY_L,             "L"),
        "KEY_SEMICOLON"    to KeyData(GLFW.GLFW_KEY_SEMICOLON,     ";"),
        "KEY_APOSTROPHE"   to KeyData(GLFW.GLFW_KEY_APOSTROPHE,    "'"),
        "KEY_GRAVE"        to KeyData(GLFW.GLFW_KEY_GRAVE_ACCENT,  "`"),
        "KEY_LSHIFT"       to KeyData(GLFW.GLFW_KEY_LEFT_SHIFT,    "L-Shift"),
        "KEY_BACKSLASH"    to KeyData(GLFW.GLFW_KEY_BACKSLASH,     "\\"),
        "KEY_Z"            to KeyData(GLFW.GLFW_KEY_Z,             "Z"),
        "KEY_X"            to KeyData(GLFW.GLFW_KEY_X,             "X"),
        "KEY_C"            to KeyData(GLFW.GLFW_KEY_C,             "C"),
        "KEY_V"            to KeyData(GLFW.GLFW_KEY_V,             "V"),
        "KEY_B"            to KeyData(GLFW.GLFW_KEY_B,             "B"),
        "KEY_N"            to KeyData(GLFW.GLFW_KEY_N,             "N"),
        "KEY_M"            to KeyData(GLFW.GLFW_KEY_M,             "M"),
        "KEY_COMMA"        to KeyData(GLFW.GLFW_KEY_COMMA,         ","),
        "KEY_PERIOD"       to KeyData(GLFW.GLFW_KEY_PERIOD,        "."),
        "KEY_SLASH"        to KeyData(GLFW.GLFW_KEY_SLASH,         "/"),
        "KEY_RSHIFT"       to KeyData(GLFW.GLFW_KEY_RIGHT_SHIFT,   "R-Shift"),
        "KEY_MULTIPLY"     to KeyData(GLFW.GLFW_KEY_KP_MULTIPLY,   "Numpad *"),
        "KEY_LMENU"        to KeyData(GLFW.GLFW_KEY_LEFT_ALT,      "L-Alt"),
        "KEY_SPACE"        to KeyData(GLFW.GLFW_KEY_SPACE,         "Space"),
        "KEY_CAPITAL"      to KeyData(GLFW.GLFW_KEY_CAPS_LOCK,     "Caps Lock"),
        "KEY_F1"           to KeyData(GLFW.GLFW_KEY_F1,            "F1"),
        "KEY_F2"           to KeyData(GLFW.GLFW_KEY_F2,            "F2"),
        "KEY_F3"           to KeyData(GLFW.GLFW_KEY_F3,            "F3"),
        "KEY_F4"           to KeyData(GLFW.GLFW_KEY_F4,            "F4"),
        "KEY_F5"           to KeyData(GLFW.GLFW_KEY_F5,            "F5"),
        "KEY_F6"           to KeyData(GLFW.GLFW_KEY_F6,            "F6"),
        "KEY_F7"           to KeyData(GLFW.GLFW_KEY_F7,            "F7"),
        "KEY_F8"           to KeyData(GLFW.GLFW_KEY_F8,            "F8"),
        "KEY_F9"           to KeyData(GLFW.GLFW_KEY_F9,            "F9"),
        "KEY_F10"          to KeyData(GLFW.GLFW_KEY_F10,           "F10"),
        "KEY_NUMLOCK"      to KeyData(GLFW.GLFW_KEY_NUM_LOCK,      "Num Lock"),
        "KEY_SCROLL"       to KeyData(GLFW.GLFW_KEY_SCROLL_LOCK,   "Scroll Lock"),
        "KEY_NUMPAD7"      to KeyData(GLFW.GLFW_KEY_KP_7,          "Numpad 7"),
        "KEY_NUMPAD8"      to KeyData(GLFW.GLFW_KEY_KP_8,          "Numpad 8"),
        "KEY_NUMPAD9"      to KeyData(GLFW.GLFW_KEY_KP_9,          "Numpad 9"),
        "KEY_SUBTRACT"     to KeyData(GLFW.GLFW_KEY_KP_SUBTRACT,   "Numpad -"),
        "KEY_NUMPAD4"      to KeyData(GLFW.GLFW_KEY_KP_4,          "Numpad 4"),
        "KEY_NUMPAD5"      to KeyData(GLFW.GLFW_KEY_KP_5,          "Numpad 5"),
        "KEY_NUMPAD6"      to KeyData(GLFW.GLFW_KEY_KP_6,          "Numpad 6"),
        "KEY_ADD"          to KeyData(GLFW.GLFW_KEY_KP_ADD,        "Numpad +"),
        "KEY_NUMPAD1"      to KeyData(GLFW.GLFW_KEY_KP_1,          "Numpad 1"),
        "KEY_NUMPAD2"      to KeyData(GLFW.GLFW_KEY_KP_2,          "Numpad 2"),
        "KEY_NUMPAD3"      to KeyData(GLFW.GLFW_KEY_KP_3,          "Numpad 3"),
        "KEY_NUMPAD0"      to KeyData(GLFW.GLFW_KEY_KP_0,          "Numpad 0"),
        "KEY_DECIMAL"      to KeyData(GLFW.GLFW_KEY_KP_DECIMAL,    "Numpad ."),
        "KEY_F11"          to KeyData(GLFW.GLFW_KEY_F11,           "F11"),
        "KEY_F12"          to KeyData(GLFW.GLFW_KEY_F12,           "F12"),
        "KEY_F13"          to KeyData(GLFW.GLFW_KEY_F13,           "F13"),
        "KEY_F14"          to KeyData(GLFW.GLFW_KEY_F14,           "F14"),
        "KEY_F15"          to KeyData(GLFW.GLFW_KEY_F15,           "F15"),
        "KEY_F16"          to KeyData(GLFW.GLFW_KEY_F16,           "F16"),
        "KEY_F17"          to KeyData(GLFW.GLFW_KEY_F17,           "F17"),
        "KEY_F18"          to KeyData(GLFW.GLFW_KEY_F18,           "F18"),
        "KEY_F19"          to KeyData(GLFW.GLFW_KEY_F19,           "F19"),
        "KEY_NUMPADEQUALS" to KeyData(GLFW.GLFW_KEY_KP_EQUAL,      "Numpad ="),
        "KEY_NUMPADENTER"  to KeyData(GLFW.GLFW_KEY_KP_ENTER,      "Numpad Enter"),
        "KEY_RCONTROL"     to KeyData(GLFW.GLFW_KEY_RIGHT_CONTROL, "R-Control"),
        "KEY_DIVIDE"       to KeyData(GLFW.GLFW_KEY_KP_DIVIDE,     "Numpad /"),
        "KEY_PRINTSCREEN"  to KeyData(GLFW.GLFW_KEY_PRINT_SCREEN,  "Print Screen"),
        "KEY_RMENU"        to KeyData(GLFW.GLFW_KEY_RIGHT_ALT,     "R-Alt"),
        "KEY_PAUSE"        to KeyData(GLFW.GLFW_KEY_PAUSE,         "Pause"),
        "KEY_HOME"         to KeyData(GLFW.GLFW_KEY_HOME,          "Home"),
        "KEY_UP"           to KeyData(GLFW.GLFW_KEY_UP,            "Up"),
        "KEY_PRIOR"        to KeyData(GLFW.GLFW_KEY_PAGE_UP,       "Prior"),
        "KEY_LEFT"         to KeyData(GLFW.GLFW_KEY_LEFT,          "Left"),
        "KEY_RIGHT"        to KeyData(GLFW.GLFW_KEY_RIGHT,         "Right"),
        "KEY_END"          to KeyData(GLFW.GLFW_KEY_END,           "End"),
        "KEY_DOWN"         to KeyData(GLFW.GLFW_KEY_DOWN,          "Down"),
        "KEY_NEXT"         to KeyData(GLFW.GLFW_KEY_PAGE_DOWN,     "Next"),
        "KEY_INSERT"       to KeyData(GLFW.GLFW_KEY_INSERT,        "Insert"),
        "KEY_DELETE"       to KeyData(GLFW.GLFW_KEY_DELETE,        "Delete"),
        "KEY_LWIN"         to KeyData(GLFW.GLFW_KEY_LEFT_SUPER,    "L-Win"),
        "KEY_RWIN"         to KeyData(GLFW.GLFW_KEY_RIGHT_SUPER,   "R-Win"),

//        // Mouse buttons — stored as (button - MOUSE_KEY_OFFSET)
//        "LEFT_MOUSE"       to KeyData(0 - MOUSE_KEY_OFFSET,        "L-Mouse"),
//        "RIGHT_MOUSE"      to KeyData(1 - MOUSE_KEY_OFFSET,        "R-Mouse"),
//        "MIDDLE_MOUSE"     to KeyData(2 - MOUSE_KEY_OFFSET,        "Middle Mouse"),
//        "MOUSE_4"          to KeyData(3 - MOUSE_KEY_OFFSET,        "Mouse 4"),
//        "MOUSE_5"          to KeyData(4 - MOUSE_KEY_OFFSET,        "Mouse 5"),
//        "MOUSE_6"          to KeyData(5 - MOUSE_KEY_OFFSET,        "Mouse 6"),
//        "MOUSE_7"          to KeyData(6 - MOUSE_KEY_OFFSET,        "Mouse 7"),
//        "MOUSE_8"          to KeyData(7 - MOUSE_KEY_OFFSET,        "Mouse 8"),
//        "MOUSE_9"          to KeyData(8 - MOUSE_KEY_OFFSET,        "Mouse 9"),
    )

    val keycodeToKeyName: Map<Int, String> = buildMap {
        for ((name, data) in keyNameToKeyData) {
            put(data.glfwKeycode, name)
        }
    }

    val modifierKeyNames: Set<String> = setOf(
        "KEY_LCONTROL", "KEY_RCONTROL",
        "KEY_LSHIFT",   "KEY_RSHIFT",
        "KEY_LMENU",    "KEY_RMENU",
    )

    val modifierKeyCodes: Set<Int> = modifierKeyNames
        .mapNotNull { keyNameToKeyData[it]?.glfwKeycode }
        .toSet()

    private val shiftedCharacters: Map<Char, Char> = mapOf(
        '1' to '!', '2' to '@', '3' to '#', '4' to '$', '5' to '%',
        '6' to '^', '7' to '&', '8' to '*', '9' to '(', '0' to ')',
        '-' to '_', '=' to '+', '[' to '{', ']' to '}', '\\' to '|',
        ';' to ':', '\'' to '"', ',' to '<', '.' to '>', '/' to '?',
        '`' to '~',
    )

    /** Returns the GLFW keycode for [keyName], or null if unknown. */
    fun getKeyCode(keyName: String): Int? {
        val data = keyNameToKeyData[keyName] ?: return null
        return data.glfwKeycode
    }

    /** Returns the key name string for a GLFW [keyCode], or "KEY_UNKNOWN". */
    fun getKeyName(keyCode: Int): String {
        val name = keycodeToKeyName[keyCode] ?: return "KEY_UNKNOWN"
        return name
    }

    /** Returns the human-readable display name for a key name string. */
    fun getKeyNamePrettyName(keyName: String): String {
        if (keyName == "KEY_UNKNOWN") return "Unknown"
        val data = keyNameToKeyData[keyName] ?: return "Unknown"
        return data.prettyName
    }

    /** Returns the human-readable display name for a GLFW keycode. */
    fun getKeyCodePrettyName(keyCode: Int): String =
        getKeyNamePrettyName(getKeyName(keyCode))

    /**
     * Returns [char] shifted (e.g. '1' → '!') when Shift is held,
     * or the character uppercased for letter keys.
     */
    fun getModifiedCharacter(char: Char): Char {
        if (!isShiftDown()) return char
        return shiftedCharacters[char] ?: char.uppercaseChar()
    }

    /** True while the named key is physically held down. */
    fun isKeyNameDown(keyName: String): Boolean =
        isKeyCodeDown(getKeyCode(keyName))

    /**
     * True while [keyCode] is physically held down.
     * Mouse buttons (negative codes) are tested via GLFW mouse button query.
     */
    fun isKeyCodeDown(keyCode: Int?): Boolean {
        if (keyCode == null) return false
        val window = Minecraft.getInstance().window.handle()
        val state = if (keyCode < 20) GLFW.glfwGetMouseButton(window, keyCode) else GLFW.glfwGetKey(window, keyCode)
        return state == GLFW.GLFW_PRESS
    }

    fun isModifierKeyName(keyName: String): Boolean = keyName in modifierKeyNames
    fun isModifierKeyCode(keyCode: Int): Boolean    = keyCode in modifierKeyCodes

    fun isShiftDown(): Boolean = isKeyNameDown("KEY_LSHIFT") || isKeyNameDown("KEY_RSHIFT")
    fun isCtrlDown():  Boolean = isKeyNameDown("KEY_LCONTROL") || isKeyNameDown("KEY_RCONTROL")
    fun isAltDown():   Boolean = isKeyNameDown("KEY_LMENU") || isKeyNameDown("KEY_RMENU")
    fun isEscapeDown(): Boolean = isKeyNameDown("KEY_ESCAPE")

    /**
     * Returns a formatted string like "Ctrl + Shift + G" for a key combo.
     * [modifiers] is a [KeyModifiers] instance describing which modifiers apply.
     */
    fun getKeyComboName(keyName: String, modifiers: KeyModifiers): String {
        if (keyName == "KEY_UNKNOWN") return "Unknown"
        return buildList {
            if (modifiers.ctrl)  add("Ctrl")
            if (modifiers.shift) add("Shift")
            if (modifiers.alt)   add("Alt")
            add(getKeyNamePrettyName(keyName))
        }.joinToString(" + ")
    }
}
