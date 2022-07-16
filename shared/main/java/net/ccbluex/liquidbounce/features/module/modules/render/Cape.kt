/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.util.ResourceLocation
import java.util.*
import java.util.Locale.*


@ModuleInfo(name = "Cape", description = "LiquidBounce capes.", category = ModuleCategory.RENDER)
class Cape : Module() {

    val styleValue = ListValue("Style", arrayOf("LBDark", "LBLight", "Lunar","PowerX","Badlion","Novoline","Hanabi","XiGua"), "Lunar")

    fun getCapeLocation(value: String): ResourceLocation {
        return try {
            CapeStyle.valueOf(value.toUpperCase()).location
        } catch (e: IllegalArgumentException) {
            CapeStyle.LBDARK.location
        }
    }

    enum class CapeStyle(val location: ResourceLocation) {
        LBDARK(ResourceLocation("liquidbounce/cape/dark.png")),
        LBLIGHT(ResourceLocation("liquidbounce/cape/light.png")),
        LUNAR(ResourceLocation("liquidbounce/cape/lunar.png")),
        POWERX(ResourceLocation("liquidbounce/cape/powerx.png")),
        BADLINO(ResourceLocation("liquidbounce/cape/badlion.png")),
        NOVOLINE(ResourceLocation("liquidbounce/cape/novoline.png")),
        HANABI(ResourceLocation("liquidbounce/cape/Hanabi.png")),
        XIGUA(ResourceLocation("liquidbounce/cape/gua.png"))
    }

    override val tag: String
        get() = styleValue.get()
}