/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.api.minecraft.network

import net.ccbluex.liquidbounce.api.minecraft.network.handshake.client.ICPacketHandshake
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.*
import net.ccbluex.liquidbounce.api.minecraft.network.play.server.*

interface IPacket {
    fun asSPacketAnimation(): ISPacketAnimation
    fun asSPacketEntity(): ISPacketEntity
    fun asCPacketPlayer(): ICPacketPlayer
    fun asCPacketUseEntity(): ICPacketUseEntity
    fun asSPacketEntityVelocity(): ISPacketEntityVelocity
    fun asCPacketChatMessage(): ICPacketChatMessage
    fun asSPacketCloseWindow(): ISPacketCloseWindow
    fun asSPacketTabComplete(): ISPacketTabComplete
    fun asSPacketPosLook(): ISPacketPosLook
    fun asSPacketResourcePackSend(): ISPacketResourcePackSend
    fun asCPacketHeldItemChange(): ICPacketHeldItemChange
    fun asSPacketWindowItems(): ISPacketWindowItems
    fun asCPacketCustomPayload(): ICPacketCustomPayload
    fun asCPacketHandshake(): ICPacketHandshake
}
interface IPacketF<T> : IPacket {
    override fun asSPacketAnimation(): ISPacketAnimation
    override fun asSPacketEntity(): ISPacketEntity
    override fun asCPacketPlayer(): ICPacketPlayer
    override fun asCPacketUseEntity(): ICPacketUseEntity
    override fun asSPacketEntityVelocity(): ISPacketEntityVelocity
    override fun asCPacketChatMessage(): ICPacketChatMessage
    override fun asSPacketCloseWindow(): ISPacketCloseWindow
    override fun asSPacketTabComplete(): ISPacketTabComplete
    override fun asSPacketPosLook(): ISPacketPosLook
    override fun asSPacketResourcePackSend(): ISPacketResourcePackSend
    override fun asCPacketHeldItemChange(): ICPacketHeldItemChange
    override fun asSPacketWindowItems(): ISPacketWindowItems
    override fun asCPacketCustomPayload(): ICPacketCustomPayload
    override fun asCPacketHandshake(): ICPacketHandshake
}