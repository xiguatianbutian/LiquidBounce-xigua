/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code was taken from UnlegitMC/FDPClient, modified. Please credit them and us when using this code in your repository.
 */
package Xigua.AutoMusic

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.util.IIChatComponent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
//import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
//import net.minecraft.event.ClickEvent
//import net.minecraft.network.play.client.*
//import net.ccbluex.liquidbounce.injection.backend.IChatComponentImpl
//import net.minecraft.network.play.server.SPacketSetSlot

import java.util.*
import kotlin.concurrent.schedule

@ModuleInfo(name = "AutoPlay", description = "Automatically move you to another game after finishing it.", category = ModuleCategory.MISC)
class AutoPlay : Module() {
//    private var clickState = 0
//    private val modeValue = ListValue("Server", arrayOf("HYT"), "HYT")
//    private val ModeValue = ListValue("Mode", arrayOf("4v4", "16", "32/64", "Skywar"), "4v4")
//
//
//    private var clicking = false
//    private var queued = false
//    private var waitForLobby = false
//
//    override fun onEnable() {
//        clickState = 0
//        clicking = false
//        queued = false
//        waitForLobby = false
//    }
//
//    @EventTarget
//    fun onPacket(event: PacketEvent) {
//        val packet = event.packet
//        if (packet is SPacketSetSlot) {
//
//            when (modeValue.get().toLowerCase()) {
//                "HYT"->{
//                    val text = IIChatComponent.unformattedText
//                    if(text.contains("起床战争>>恭喜！蓝之队", true)){
//
//                    }
//                }
//                "redesky" -> {
//                    if (clickState == 0 && windowId == 0 && slot == 42 && itemName.contains("paper", ignoreCase = true) && displayName.contains("Jogar novamente", ignoreCase = true)) {
//                        clickState = 1
//                        clicking = true
//                        queueAutoPlay {
//                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(6))
//                            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(item))
//                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
//                            clickState = 2
//                        }
//                    } else if (clickState == 2 && windowId != 0 && slot == 11 && itemName.contains("enderPearl", ignoreCase = true)) {
//                        Timer().schedule(500L) {
//                            clicking = false
//                            clickState = 0
//                            mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slot, 0, 0, item, 1919))
//                        }
//                    }
//                }
//                "blocksmc", "hypixel" -> {
//                    if (clickState == 0 && windowId == 0 && slot == 43 && itemName.contains("paper", ignoreCase = true)) {
//                        queueAutoPlay {
//                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(7))
//                            repeat(2) {
//                                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(item))
//                            }
//                        }
//                        clickState = 1
//                    }
//                    if (modeValue.equals("hypixel") && clickState == 1 && windowId != 0 && itemName.equals("item.fireworks", ignoreCase = true)) {
//                        mc.netHandler.addToSendQueue(C0EPacketClickWindow(windowId, slot, 0, 0, item, 1919))
//                        mc.netHandler.addToSendQueue(C0DPacketCloseWindow(windowId))
//                    }
//                }
//            }
//        } else if (packet is S02PacketChat) {
//            val text = packet.chatComponent.unformattedText
//            when (modeValue.get().toLowerCase()) {
//                "minemora" -> {
//                    if (text.contains("Has click en alguna de las siguientes opciones", true)) {
//                        queueAutoPlay {
//                            mc.thePlayer.sendChatMessage("/join")
//                        }
//                    }
//                }
//                "blocksmc" -> {
//                    if (clickState == 1 && text.contains("Only VIP players can join full servers!", true)) {
//                        LiquidBounce.hud.addNotification(Notification("Join failed! trying again...", Notification.Type.WARNING, 3000L))
//                        // connect failed so try to join again
//                        Timer().schedule(1500L) {
//                            mc.netHandler.addToSendQueue(C09PacketHeldItemChange(7))
//                            repeat(2) {
//                                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
//                            }
//                        }
//                    }
//                }
//                "jartex" -> {
//                    val component = packet.chatComponent
//                    if (text.contains("Click here to play again", true)) {
//                        component.siblings.forEach { sib ->
//                            val clickEvent = sib.chatStyle.chatClickEvent
//                            if(clickEvent != null && clickEvent.action == ClickEvent.Action.RUN_COMMAND && clickEvent.value.startsWith("/")) {
//                                //println("clickEvent: ${clickEvent.value}")
//                                queueAutoPlay {
//                                    mc.thePlayer.sendChatMessage(clickEvent.value)
//                                }
//                            }
//                        }
//                    }
//                }
//                "hypixel" -> {
//                    fun process(component: IChatComponent) {
//                        val value = component.chatStyle.chatClickEvent?.value
//                        if (value != null && value.startsWith("/play", true)) {
//                            queueAutoPlay {
//                                mc.thePlayer.sendChatMessage(value)
//                            }
//                        }
//                        component.siblings.forEach {
//                            process(it)
//                        }
//                    }
//                    process(packet.chatComponent)
//                }
//                "minefc/heromc_bedwars" -> {
//                    if (text.contains("Bạn đã bị loại!", false)
//                        || text.contains("đã thắng trò chơi", false)) {
//                        mc.thePlayer.sendChatMessage("/bw leave")
//                        waitForLobby = true
//                    }
//                    if (((waitForLobby || autoStartValue.get()) && text.contains("¡Hiển thị", false))
//                        || (replayWhenKickedValue.get() && text.contains("[Anticheat] You have been kicked from the server!", false))) {
//                        queueAutoPlay {
//                            mc.thePlayer.sendChatMessage("/bw join ${bwModeValue.get()}")
//                        }
//                        waitForLobby = false
//                    }
//                    if (showGuiWhenFailedValue.get() && text.contains("giây", false) && text.contains("thất bại", false)) {
//                        LiquidBounce.hud.addNotification(Notification("Failed to join, showing GUI...", Notification.Type.ERROR, 1000L))
//                        mc.thePlayer.sendChatMessage("/bw gui ${bwModeValue.get()}")
//                    }
//                }
//            }
//        }
//    }
//
//    private fun queueAutoPlay(delay: Long = delayValue.get().toLong() * 1000, runnable: () -> Unit) {
//        if (queued)
//            return
//        queued = true
//        AutoDisable.handleGameEnd()
//        if (this.state) {
//            Timer().schedule(delay) {
//                queued = false
//                if (state) {
//                    runnable()
//                }
//            }
//            LiquidBounce.hud.addNotification(Notification("Sending you to next game in ${delayValue.get()}s...", Notification.Type.INFO, delayValue.get().toLong() * 1000L))
//        }
//    }
//
//    @EventTarget
//    fun onWorld(event: WorldEvent) {
//        clicking = false
//        clickState = 0
//        queued = false
//    }
//
//    override val tag: String
//        get() = modeValue.get()
}
