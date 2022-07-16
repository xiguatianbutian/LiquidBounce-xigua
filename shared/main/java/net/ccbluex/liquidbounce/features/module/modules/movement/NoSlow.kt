/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.WEnumHand
import net.ccbluex.liquidbounce.api.minecraft.item.IItem
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.chat.packet.packets.Packet
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.injection.backend.Backend
import net.ccbluex.liquidbounce.injection.backend.WrapperImpl
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.createUseItemPacket
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
//import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerPosition

@ModuleInfo(name = "NoSlow", description = "Cancels slowness effects caused by soulsand and using items.",
        category = ModuleCategory.MOVEMENT)
class NoSlow : Module() {

    // Highly customizable values

    private val msTimer = MSTimer()
    private val modeValue = ListValue("PacketMode", arrayOf("Vanilla", "Watchdog", "OldWatchdog", "OldHypixel", "Blink", "Experimental", "NCP", "AAC", "AAC5", "Custom"), "Vanilla")
    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val customRelease = BoolValue("CustomReleasePacket", false)
    private val customPlace = BoolValue("CustomPlacePacket", false)
    private val customOnGround = BoolValue("CustomOnGround", false)
    private val customDelayValue = IntegerValue("CustomDelay", 60, 0, 1000)
    private val testValue = BoolValue("SendPacket", false)
    private val ciucValue = BoolValue("CheckInUseCount", true)
    private val packetTriggerValue = ListValue("PacketTrigger", arrayOf("PreRelease", "PostRelease"), "PostRelease")
    private val debugValue = BoolValue("Debug", false)

    // Blocks
    val soulsandValue = BoolValue("Soulsand", true)
    val liquidPushValue = BoolValue("LiquidPush", true)


//    private val blinkPackets = mutableListOf<Packet<ICPacketPlayerPosition>>()
    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0
    private var lastOnGround = false

    private var fasterDelay = false
    private var placeDelay = 0L
    private val timer = MSTimer()

    override fun onEnable() {
//        blinkPackets.clear()
        msTimer.reset()
    }

    override fun onDisable() {
//        blinkPackets.forEach {
//            PacketUtils.sendPacketNoEvent(it)
//        }
//        blinkPackets.clear()
    }


    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return
        val heldItem = thePlayer.heldItem ?: return

        if (!classProvider.isItemSword(heldItem.item) || !MovementUtils.isMoving)
            return

        val aura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
        if (!thePlayer.isBlocking && !aura.blockingStatus)
            return

//        if (event.packet.get() && Backend.MINECRAFT_VERSION_MINOR == 8) {
//            when (event.eventState) {
//                EventState.PRE -> {
//                    val digging = classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM, WBlockPos(0, 0, 0), classProvider.getEnumFacing(EnumFacingType.DOWN))
//                    mc.netHandler.addToSendQueue(digging)
//                }
//                EventState.POST -> {
//                    val blockPlace = classProvider.createCPacketPlayerBlockPlacement(WBlockPos(-1, -1, -1), 255, mc.thePlayer!!.inventory.getCurrentItemInHand(), 0.0F, 0.0F, 0.0F)
//                    mc.netHandler.addToSendQueue(blockPlace)
//                }
//            }
//        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer!!.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: IItem?, isForward: Boolean): Float {
        return when {
            classProvider.isItemFood(item) || classProvider.isItemPotion(item) || classProvider.isItemBucketMilk(item) -> {
                if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
            }
            classProvider.isItemSword(item) -> {
                if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
            }
            classProvider.isItemBow(item) -> {
                if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
            }
            else -> 0.2F
        }
    }

}
