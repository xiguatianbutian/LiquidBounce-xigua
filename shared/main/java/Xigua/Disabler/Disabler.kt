/*
 * LiquidBounce Hacked Client
 * Thank you FDP
 * Thank you for writing the code separately
 * FUCK!
 */
package Xigua.Disabler

import net.ccbluex.liquidbounce.api.minecraft.network.IPacketF
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.PacketEventF
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import java.util.*

@ModuleInfo(name = "Disabler", description = "Disable some anticheats' checks.", category = ModuleCategory.EXPLOIT)
class Disabler : Module() {
    private val debugValue = BoolValue("Debug", false)
    fun debugMessage(str: String) {
        if (debugValue.get()) {
            alert("§7[§c§lDisabler§7] §b$str")
        }
    }
    val modeValue = ListValue("Mode",
        arrayOf(
            "VulcanCombat"
        ), "VulcanCombat")
    private var currentTrans = 0
    private var vulTickCounterUID = 0
    private val packetBuffer = LinkedList<IPacketF<INetHandlerPlayServer>>()
    private val packetBufferN = LinkedList<Packet<*>>()
    private val lagTimer = MSTimer()
    override fun onEnable() {
        vulTickCounterUID = -25767
        debugMessage("VulcanCombat Disabler §c§lONLY §r§awork when you rejoined the server!")
    }
    fun onUpdate(event: UpdateEvent) {
        if(lagTimer.hasTimePassed(5000L) && packetBuffer.size > 4) {
            lagTimer.reset()
            while (packetBuffer.size > 4) {
                PacketUtils.sendPacketNoEvent(packetBuffer.poll())
            }
        }
    }
    fun onWorld(event: WorldEvent) {
        currentTrans = 0
        packetBuffer.clear()
        lagTimer.reset()
        vulTickCounterUID = -25767
    }
    fun onPacket(event: PacketEventF) {
        val packet = event.packet
        if (packet is CPacketConfirmTransaction) {
            if (Math.abs((Math.abs((packet.uid).toInt()).toInt() - Math.abs(vulTickCounterUID.toInt()).toInt()).toInt()) <= 4) {
                vulTickCounterUID = (packet.uid).toInt()
                packetBufferN.add(packet)
                event.cancelEvent()
                debugMessage("C0F-PingTickCounter IN ${packetBuffer.size}")
            }else if (Math.abs((Math.abs((packet.uid).toInt()).toInt() - 25767).toInt()) <= 4) {
                vulTickCounterUID = (packet.uid).toInt()
                debugMessage("C0F-PingTickCounter RESETED")
            }
        }
    }
}
