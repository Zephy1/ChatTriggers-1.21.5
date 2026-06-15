package com.chattriggers.ctjs.api.client

import com.chattriggers.ctjs.api.inventory.Slot
import com.chattriggers.ctjs.api.message.TextComponent
import com.chattriggers.ctjs.api.world.World
import com.chattriggers.ctjs.internal.listeners.ClientListener
import com.chattriggers.ctjs.internal.mixins.ChatScreenAccessor
import com.chattriggers.ctjs.internal.mixins.HandledScreenAccessor
import com.chattriggers.ctjs.internal.mixins.KeyBindingAccessor
import com.chattriggers.ctjs.internal.utils.asMixin
import com.mojang.realmsclient.RealmsMainScreen
import gg.essential.universal.UKeyboard
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.ConnectScreen
import net.minecraft.client.gui.screens.TitleScreen
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.ServerData
import net.minecraft.client.multiplayer.resolver.ServerAddress
import kotlin.math.max
import kotlin.math.roundToInt

//#if MC>=12106
import net.minecraft.text.Text
//#endif

object Client {
    internal var referenceSystemTime: Long = 0

    @JvmField
    val currentGui = CurrentGuiWrapper()

    @JvmField
    val camera = CameraWrapper()

    @JvmField
    val settings = Settings

    /**
     * Gets Minecraft's Minecraft object
     *
     * @return The Minecraft object
     */
    @JvmStatic
    fun getMinecraft(): Minecraft = Minecraft.getInstance()

    /**
     * Gets Minecraft's NetHandlerPlayClient object
     *
     * @return The NetHandlerPlayClient object
     */
    @JvmStatic
    fun getConnection(): ClientPacketListener? = getMinecraft().connection

    /**
     * Schedule's a task to run on Minecraft's main thread in [delay] ticks.
     * Defaults to the next tick.
     * @param delay The delay in ticks
     * @param callback The task to run on the main thread
     */
    @JvmStatic
    @JvmOverloads
    fun scheduleTask(delay: Int = 0, callback: () -> Unit) {
        ClientListener.addTask(delay, callback)
    }

    //#if MC>=12110
    fun <T> synchronizedTask(task: () -> T): T {
        val mc = getMinecraft()
        if (mc.isSameThread) {
            return task()
        }

        val latch = java.util.concurrent.CountDownLatch(1)
        var result: T? = null

        mc.execute {
            try {
                result = task()
            } finally {
                latch.countDown()
            }
        }

        latch.await()
        return result!!
    }
    //#endif

    /**
     * Quits the client back to the main menu.
     * This acts just like clicking the "Disconnect" or "Save and quit to title" button.
     */
    @JvmStatic
    fun disconnect() {
        scheduleTask {
            //#if MC<=12105
            //$$World.toMC()?.disconnect()
            //$$getMinecraft().disconnect()
            //#else
            World.toMC()?.disconnect(Text.empty())
            //#endif

            getMinecraft().setScreen(
                when {
                    getMinecraft().isLocalServer -> TitleScreen()
                    getMinecraft().getCurrentServer?.isRealm == true -> RealmsMainScreen(TitleScreen())
                    else -> JoinMultiplayerScreen(TitleScreen())
                },
            )
        }
    }

    /**
     * Connects to the server with the given ip.
     * @param ip The ip to connect to
     */
    @JvmStatic
    @JvmOverloads
    fun connect(ip: String, port: Int = 25565) {
        scheduleTask {
            ConnectScreen.connect(
                JoinMultiplayerScreen(TitleScreen()),
                getMinecraft(),
                ServerAddress(ip, port),
                ServerData("Server", ip, ServerData.Type.OTHER),
                false,
                null,
            )
        }
    }

    /**
     * Gets the Minecraft ChatHud object for the chat gui
     *
     * @return The GuiNewChat object for the chat gui
     */
    @JvmStatic
    fun getChatGui(): ChatComponent? = getMinecraft().inGameHud?.chatHud

    @JvmStatic
    fun isInChat(): Boolean = getMinecraft().currentScreen is ChatScreen

    @JvmStatic
    fun getTabGui(): PlayerListHud? = getMinecraft().inGameHud?.playerListHud

    @JvmStatic
    fun isInTab(): Boolean = getMinecraft().options.playerListKey.isPressed

    /**
     * Gets whether the Minecraft window is active
     * and in the foreground of the user's screen.
     *
     * @return true if the game is active, false otherwise
     */
    @JvmStatic
    fun isTabbedIn(): Boolean = getMinecraft().isWindowFocused

    @JvmStatic
    fun isControlDown(): Boolean = UKeyboard.isCtrlKeyDown()

    @JvmStatic
    fun isShiftDown(): Boolean = UKeyboard.isShiftKeyDown()

    @JvmStatic
    fun isAltDown(): Boolean = UKeyboard.isAltKeyDown()

    @JvmStatic
    fun getFPS(): Int = getMinecraft().currentFps

    @JvmStatic
    fun getVersion(): String = getMinecraft().gameVersion

    @JvmStatic
    fun getMaxMemory(): Long = Runtime.getRuntime().maxMemory()

    @JvmStatic
    fun getTotalMemory(): Long = Runtime.getRuntime().totalMemory()

    @JvmStatic
    fun getFreeMemory(): Long = Runtime.getRuntime().freeMemory()

    @JvmStatic
    fun getMemoryUsage(): Int = ((getTotalMemory() - getFreeMemory()) * 100 / getMaxMemory().toFloat()).roundToInt()

    @JvmStatic
    fun getSystemTime(): Long = (System.nanoTime() - referenceSystemTime) / 1_000_000

    @JvmStatic
    fun getMouseX() = getMinecraft().mouse.x * getMinecraft().window.scaledWidth / max(1, getMinecraft().window.scaledWidth)

    @JvmStatic
    fun getMouseY() = getMinecraft().mouse.y * getMinecraft().window.scaledHeight / max(1, getMinecraft().window.scaledHeight)

    @JvmStatic
    fun isInGui(): Boolean = currentGui.get() != null

    /**
     * Gets the chat message currently typed into the chat gui.
     *
     * @return A blank string if the gui isn't open, otherwise, the message
     */
    @JvmStatic
    fun getCurrentChatMessage(): String {
        return if (isInChat()) {
            val chatGui = getMinecraft().currentScreen as ChatScreen
            chatGui.asMixin<ChatScreenAccessor>().chatField.text
        } else {
            ""
        }
    }

    /**
     * Sets the current chat message, if the chat gui is not open, one will be opened.
     *
     * @param message the message to put in the chat text box.
     */
    @JvmStatic
    fun setCurrentChatMessage(message: String) {
        if (isInChat()) {
            val chatGui = getMinecraft().currentScreen as ChatScreen
            chatGui.asMixin<ChatScreenAccessor>().chatField.text = message
        } else {
            //#if MC<=12108
            //$$currentGui.set(ChatScreen(message))
            //#else
            currentGui.set(ChatScreen(message, false))
            //#endif
        }
    }

    @JvmStatic
    fun sendPacket(packet: Packet<*>) {
        getConnection()?.connection?.send(packet)
    }

    /**
     * Display a title.
     *
     * @param title title text
     * @param subtitle subtitle text
     * @param fadeIn time to fade in
     * @param time time to stay on screen
     * @param fadeOut time to fade out
     */
    @JvmStatic
    fun showTitle(title: String?, subtitle: String?, fadeIn: Int, time: Int, fadeOut: Int) {
        getMinecraft().inGameHud.apply {
            setTitleTicks(fadeIn, time, fadeOut)
            if (title != null) {
                setTitle(TextComponent(title))
            }
            if (subtitle != null) {
                setSubtitle(TextComponent(subtitle))
            }
        }
    }

    /**
     * Copies a string to the clipboard
     *
     * @param text The text to copy
     */
    @JvmStatic
    @JvmOverloads
    fun copy(text: String = "") {
        getMinecraft().keyboard.clipboard = text
    }

    /**
     * Get the string currently on the clipboard
     */
    @JvmStatic
    fun paste(): String = getMinecraft().keyboard.clipboard

    /**
     * Get the [KeyBinding] from an already existing Minecraft KeyBinding, otherwise, returns null.
     *
     * @param keyCode the keycode to search for, see Keyboard below. Ex. Keyboard.KEY_A
     * @return the [KeyBinding] from a Minecraft KeyBinding, or null if one doesn't exist
     * @see [org.lwjgl.input.Keyboard](http://legacy.lwjgl.org/javadoc/org/lwjgl/input/Keyboard.html)
     */
    @JvmStatic
    fun getKeyBindFromKey(keyCode: Int): KeyBind? {
        return KeyBind.getKeyBinds().find {
            it.getKeyCode() == keyCode
        } ?: getMinecraft()
            .options
            .allKeys
            .find {
                it.asMixin<KeyBindingAccessor>().boundKey.code == keyCode
            }?.let(::KeyBind)
    }

    /**
     * Get the [KeyBinding] from an already existing Minecraft KeyBinding, else, return a new one.
     *
     * @param keyCode the keycode which the keybind will respond to, see Keyboard below. Ex. Keyboard.KEY_A
     * @param description the description of the keybind
     * @param category the keybind category the keybind will be in
     * @return the [KeyBinding] from a Minecraft KeyBinding, or a new one if one doesn't exist
     * @see [org.lwjgl.input.Keyboard](http://legacy.lwjgl.org/javadoc/org/lwjgl/input/Keyboard.html)
     */
    @JvmStatic
    @JvmOverloads
    fun getKeyBindFromKey(keyCode: Int, description: String, category: String = "ChatTriggers"): KeyBind {
        return getKeyBindFromKey(keyCode) ?: KeyBind(description, keyCode, category)
    }

    /**
     * Get the [KeyBinding] from an already existing
     * Minecraft KeyBinding, otherwise, returns null.
     *
     * @param description the description of the keybind
     * @return the [KeyBinding], or null if one doesn't exist
     */
    @JvmStatic
    fun getKeyBindFromDescription(description: String): KeyBind? {
        return KeyBind.getKeyBinds()
            .find { it.getDescription() == description }
            ?: getMinecraft().options.allKeys
                //#if MC<=12108
                //$$.find { it.translationKey == description }
                //#else
                .find { it.boundKeyTranslationKey == description }
                //#endif
                ?.let(::KeyBind)
    }

    class CurrentGuiWrapper {
        /**
         * Gets the Java class name of the currently open gui, for example, "GuiChest"
         *
         * @return the class name of the current gui
         */
        fun getClassName(): String = get()?.javaClass?.simpleName ?: "null"

        /**
         * Gets the Minecraft gui class that is currently open
         *
         * @return the Minecraft gui
         */
        fun get(): Screen? = getMinecraft().currentScreen

        fun set(screen: Screen?) {
            scheduleTask {
                getMinecraft().setScreen(screen)
            }
        }

        /**
         * Gets the slot under the mouse in the current gui, if one exists.
         *
         * @return the [Slot] under the mouse
         */
        fun getSlotUnderMouse(): Slot? {
            val screen: Screen? = get()
            return if (screen is HandledScreen<*>) {
                screen.asMixin<HandledScreenAccessor>().invokeGetSlotAt(getMouseX(), getMouseY())?.let(::Slot)
            } else {
                null
            }
        }

        /**
         * Closes the currently open gui
         */
        fun close() {
            scheduleTask { Player.toMC()?.closeScreen() }
        }
    }

    class CameraWrapper {
        //#if MC<=12110
        //$$fun getX(): Double = getMinecraft().gameRenderer.camera.pos.x
        //$$fun getY(): Double = getMinecraft().gameRenderer.camera.pos.y
        //$$fun getZ(): Double = getMinecraft().gameRenderer.camera.pos.z
        //#else
        fun getX(): Double = getMinecraft().gameRenderer.camera.cameraPos.x
        fun getY(): Double = getMinecraft().gameRenderer.camera.cameraPos.y
        fun getZ(): Double = getMinecraft().gameRenderer.camera.cameraPos.z
        //#endif
    }
}
