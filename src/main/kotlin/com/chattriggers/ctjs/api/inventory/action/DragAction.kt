package com.chattriggers.ctjs.api.inventory.action

import com.chattriggers.ctjs.api.inventory.CTClickType

//#if MC<=12111
//$$import net.minecraft.world.inventory.ClickType
//#else
import net.minecraft.world.inventory.ContainerInput
//#endif

class DragAction(slot: Int, windowId: Int) : Action(slot, windowId) {
    private lateinit var clickType: CTClickType
    private lateinit var stage: Stage

    fun getClickType(): CTClickType = clickType

    /**
     * The type of click (REQUIRED)
     *
     * @param clickType the new click type
     */
    fun setClickType(clickType: CTClickType) = apply {
        this.clickType = clickType
    }

    fun getStage(): Stage = stage

    /**
     * The stage of this drag (REQUIRED)
     * BEGIN is when beginning the drag
     * SLOT is for each slot being dragged into
     * END is for ending the drag
     *
     * @param stage the stage
     */
    fun setStage(stage: Stage) = apply {
        this.stage = stage
    }

    /**
     * Sets the type of click.
     * Possible values are: LEFT, RIGHT, MIDDLE
     *
     * @param clickType the click type
     * @return the current Action for method chaining
     */
    fun setClickString(clickType: String) = apply {
        this.clickType = CTClickType.valueOf(clickType.uppercase())
    }

    /**
     * Sets the stage of this drag.
     * Possible values are: BEGIN, SLOT, END [stage]
     *
     * @param stage the stage
     * @return the current Action for method chaining
     */
    fun setStageString(stage: String) = apply {
        this.stage = Stage.valueOf(stage.uppercase())
    }

    override fun complete() {
        val button = stage.stage and 3 or (clickType.button and 3) shl 2

        if (stage != Stage.SLOT) {
            slot = -999
            println("Enforcing slot of -999")
        }

        //#if MC<=12111
        //$$doClick(button, ClickType.QUICK_CRAFT)
        //#else
        doClick(button, ContainerInput.QUICK_CRAFT)
        //#endif
    }

    enum class Stage(val stage: Int) {
        BEGIN(0),
        SLOT(1),
        END(2),
        ;
    }
}
