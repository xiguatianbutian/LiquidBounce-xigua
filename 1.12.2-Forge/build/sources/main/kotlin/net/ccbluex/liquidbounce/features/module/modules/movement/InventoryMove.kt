/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.ClickWindowEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.settings.GameSettings

@ModuleInfo(name = "InventoryMove", description = "Allows you to walk while an inventory is opened.", category = ModuleCategory.MOVEMENT)
class InventoryMove : Module() {

    private val noDetectableValue = BoolValue("NoDetectable", false)
    val aacAdditionProValue = BoolValue("AACAP", true)
    private val noMoveClicksValue = BoolValue("NoMoveClicks", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.currentScreen !is GuiChat && mc.currentScreen !is GuiIngameMenu && (!noDetectableValue.get() || mc.currentScreen !is GuiContainer)) {
            mc.gameSettings.keyBindForward.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindForward)
            mc.gameSettings.keyBindBack.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindBack)
            mc.gameSettings.keyBindRight.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindRight)
            mc.gameSettings.keyBindLeft.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindLeft)
            mc.gameSettings.keyBindJump.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump)
            mc.gameSettings.keyBindSprint.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindSprint)
        }
    }

    @EventTarget
    fun onClick(event: ClickWindowEvent) {
        if (noMoveClicksValue.get() && MovementUtils.isMoving())
            event.cancelEvent()
    }

    override fun onDisable() {
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindForward) || mc.currentScreen != null)
            mc.gameSettings.keyBindForward.pressed = false
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindBack) || mc.currentScreen != null)
            mc.gameSettings.keyBindBack.pressed = false
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindRight) || mc.currentScreen != null)
            mc.gameSettings.keyBindRight.pressed = false
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindLeft) || mc.currentScreen != null)
            mc.gameSettings.keyBindLeft.pressed = false
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump) || mc.currentScreen != null)
            mc.gameSettings.keyBindJump.pressed = false
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindSprint) || mc.currentScreen != null)
            mc.gameSettings.keyBindSprint.pressed = false
    }

    override val tag: String?
        get() = if (aacAdditionProValue.get()) "AACAP" else null
}
