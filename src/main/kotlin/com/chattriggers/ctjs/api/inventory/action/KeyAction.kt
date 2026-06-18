package com.chattriggers.ctjs.api.inventory.action

//#if MC<=12111
//$$import net.minecraft.world.inventory.ClickType
//#else
import net.minecraft.world.inventory.ContainerInput
//#endif

class KeyAction(slot: Int, windowId: Int) : Action(slot, windowId) {
    private var key: Int = -1

    fun getKey(): Int = key

    /**
     * Which key to act as if has been clicked (REQUIRED).
     * Options currently are 0-8, representing the hotbar keys
     *
     * @param key which key to "click"
     */
    fun setKey(key: Int) = apply {
        this.key = key
    }

    override fun complete() {
        //#if MC<=12111
        //$$doClick(key, ClickType.SWAP)
        //#else
        doClick(key, ContainerInput.SWAP)
        //#endif
    }
}
