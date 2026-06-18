package com.chattriggers.ctjs.api.inventory.action

//#if MC<=12111
//$$import net.minecraft.world.inventory.ClickType
//#else
import net.minecraft.world.inventory.ContainerInput
//#endif

class DropAction(slot: Int, windowId: Int) : Action(slot, windowId) {
    private var holdingCtrl = false

    fun getHoldingCtrl(): Boolean = holdingCtrl

    /**
     * Whether the click should act as if control is being held (defaults to false)
     *
     * @param holdingCtrl to hold ctrl or not
     */
    fun setHoldingCtrl(holdingCtrl: Boolean) = apply {
        this.holdingCtrl = holdingCtrl
    }

    override fun complete() {
        //#if MC<=12111
        //$$doClick(if (holdingCtrl) 1 else 0, ClickType.THROW)
        //#else
        doClick(if (holdingCtrl) 1 else 0, ContainerInput.THROW)
        //#endif
    }
}
