package us.timinc.mc.cobblemon.unchained

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.platform.events.PlatformEvents
import net.neoforged.fml.common.Mod
import us.timinc.mc.cobblemon.unchained.modules.HiddenBooster
import us.timinc.mc.cobblemon.unchained.modules.IvBooster
import us.timinc.mc.cobblemon.unchained.modules.ShinyBooster
import us.timinc.mc.cobblemon.unchained.modules.SpawnChainer

@Mod(UnchainedMod.MOD_ID)
object UnchainedMod {
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    const val MOD_ID = "unchained"

    init {
        PlatformEvents.SERVER_STARTED.subscribe(Priority.LOWEST) {
            ShinyBooster.initialize()
            HiddenBooster.initialize()
            IvBooster.initialize()
            SpawnChainer.initialize()
        }
    }
}