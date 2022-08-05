/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.utils.ClientUtils
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

@ModuleInfo(name = "BugUp", description = "Automatically setbacks you after falling a certain distance.", category = ModuleCategory.MOVEMENT)
class BugUp : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Blink","TeleportBack", "FlyFlag", "OnGroundSpoof", "MotionTeleport-Flag"), "FlyFlag")
    private val maxFallDistance = IntegerValue("MaxFallDistance", 10, 2, 255)
    private val maxDistanceWithoutGround = FloatValue("MaxDistanceToSetback", 2.5f, 1f, 30f)
    private val indicator = BoolValue("Indicator", true)
    private val maxFallDistValue = FloatValue("MaxFallDistance", 10F, 5F, 20F)
    private val resetMotionValue = BoolValue("ResetMotion", false).displayable { modeValue.equals("Blink") }
    private val startFallDistValue = FloatValue("BlinkStartFallDistance", 2F, 0F, 5F).displayable { modeValue.equals("Blink") }

    private val packetCache = ArrayList<ICPacketPlayerDigging>()
    private var detectedLocation: WBlockPos? = null
    private var lastFound = 0F
    private var prevX = 0.0
    private var prevY = 0.0
    private var prevZ = 0.0
    private var motionX = 0.0
    private var motionY = 0.0
    private var motionZ = 0.0
    private var lastRecY = 0.0
    private var blink = false
    private var canBlink = false
    private val autoScaffoldValue = BoolValue("BlinkAutoScaffold", true).displayable { modeValue.equals("Blink") }
    private val voidOnlyValue = BoolValue("OnlyVoid", true)


    override fun onEnable() {
        ClientUtils.getLogger().info("还没修好,后果自负")
        blink = false
        canBlink = false
    }
    override fun onDisable() {
        prevX = 0.0
        prevY = 0.0
        prevZ = 0.0
    }

    @EventTarget
    fun onUpdate(e: UpdateEvent) {
        detectedLocation = null

        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.onGround && !classProvider.isBlockAir(BlockUtils.getBlock(WBlockPos(thePlayer.posX, thePlayer.posY - 1.0,
                        thePlayer.posZ)))) {
            prevX = thePlayer.prevPosX
            prevY = thePlayer.prevPosY
            prevZ = thePlayer.prevPosZ
        }

        if (!thePlayer.onGround && !thePlayer.isOnLadder && !thePlayer.isInWater) {
            val fallingPlayer = FallingPlayer(
                    thePlayer.posX,
                    thePlayer.posY,
                    thePlayer.posZ,
                    thePlayer.motionX,
                    thePlayer.motionY,
                    thePlayer.motionZ,
                    thePlayer.rotationYaw,
                    thePlayer.moveStrafing,
                    thePlayer.moveForward
            )

            detectedLocation = fallingPlayer.findCollision(60)?.pos

            if (detectedLocation != null && abs(thePlayer.posY - detectedLocation!!.y) +
                    thePlayer.fallDistance <= maxFallDistance.get()) {
                lastFound = thePlayer.fallDistance
            }

            if (thePlayer.fallDistance - lastFound > maxDistanceWithoutGround.get()) {
                val mode = modeValue.get()

                when (mode.toLowerCase()) {
                    "teleportback" -> {
                        thePlayer.setPositionAndUpdate(prevX, prevY, prevZ)
                        thePlayer.fallDistance = 0F
                        thePlayer.motionY = 0.0
                    }
                    "flyflag" -> {
                        thePlayer.motionY += 0.1
                        thePlayer.fallDistance = 0F
                    }
                    "ongroundspoof" -> mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))

                    "motionteleport-flag" -> {
                        thePlayer.setPositionAndUpdate(thePlayer.posX, thePlayer.posY + 1f, thePlayer.posZ)
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ, true))
                        thePlayer.motionY = 0.1

                        MovementUtils.strafe()
                        thePlayer.fallDistance = 0f
                    }
                    "blink" -> {
                        if (!blink) {
                            val collide = FallingPlayer(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ, 0.0, 0.0, 0.0, 0F, 0F, 0F).findCollision(60)
                            if (canBlink && (collide == null || (mc.thePlayer!!.posY - collide.pos.y)> startFallDistValue.get())) {
                                prevX = mc.thePlayer!!.posX
                                prevY = mc.thePlayer!!.posY
                                prevZ = mc.thePlayer!!.posZ
                                motionX = mc.thePlayer!!.motionX
                                motionY = mc.thePlayer!!.motionY
                                motionZ = mc.thePlayer!!.motionZ

                                packetCache.clear()
                                blink = true
                            }

                            if (mc.thePlayer!!.onGround) {
                                canBlink = true
                            }
                        } else {
                            if (mc.thePlayer!!.fallDistance> maxFallDistValue.get()) {
                                mc.thePlayer!!.setPositionAndUpdate(prevX, prevY, prevZ)
                                if (resetMotionValue.get()) {
                                    mc.thePlayer!!.motionX = 0.0
                                    mc.thePlayer!!.motionY = 0.0
                                    mc.thePlayer!!.motionZ = 0.0
                                    mc.thePlayer!!.jumpMovementFactor = 0.00f
                                } else {
                                    mc.thePlayer!!.motionX = motionX
                                    mc.thePlayer!!.motionY = motionY
                                    mc.thePlayer!!.motionZ = motionZ
                                    mc.thePlayer!!.jumpMovementFactor = 0.00f
                                }

                                if (autoScaffoldValue.get()) {
                                    LiquidBounce.moduleManager[BlockFly::class.java]!!.state = true
                                }

                                packetCache.clear()
                                blink = false
                                canBlink = false
                            } else if (mc.thePlayer!!.onGround) {
                                blink = false

                                for (packet in packetCache) {
                                    mc.netHandler.addToSendQueue(packet)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun checkVoid(): Boolean {
        var i = (-(mc.thePlayer!!.posY-1.4857625)).toInt()
        var dangerous = true
        while (i <= 0) {
            dangerous = mc.theWorld!!.getCollisionBoxes(mc.thePlayer!!.entityBoundingBox.offset(mc.thePlayer!!.motionX * 0.5, i.toDouble(), mc.thePlayer!!.motionZ * 0.5)).isEmpty()
            i++
            if (!dangerous) break
        }
        return dangerous
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        when (modeValue.get().toLowerCase()) {
            "blink" -> {
                if (blink && (packet is ICPacketPlayerDigging)) {
                    packetCache.add(packet)
                    event.cancelEvent()
                }
            }
        }
    }
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (detectedLocation == null || !indicator.get() ||
                thePlayer.fallDistance + (thePlayer.posY - (detectedLocation!!.y + 1)) < 3)
            return

        val x = detectedLocation!!.x
        val y = detectedLocation!!.y
        val z = detectedLocation!!.z

        val renderManager = mc.renderManager

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glLineWidth(2f)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)

        RenderUtils.glColor(Color(255, 0, 0, 90))
        RenderUtils.drawFilledBox(classProvider.createAxisAlignedBB(
                x - renderManager.renderPosX,
                y + 1 - renderManager.renderPosY,
                z - renderManager.renderPosZ,
                x - renderManager.renderPosX + 1.0,
                y + 1.2 - renderManager.renderPosY,
                z - renderManager.renderPosZ + 1.0)
        )

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)

        val fallDist = floor(thePlayer.fallDistance + (thePlayer.posY - (y + 0.5))).toInt()

        RenderUtils.renderNameTag("${fallDist}m (~${max(0, fallDist - 3)} damage)", x + 0.5, y + 1.7, z + 0.5)

        classProvider.getGlStateManager().resetColor()
    }

}