package us.timinc.mc.cobblemon.unchained

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.server.ServerStartedEvent
import us.timinc.mc.cobblemon.unchained.modules.HiddenBooster
import us.timinc.mc.cobblemon.unchained.modules.IvBooster
import us.timinc.mc.cobblemon.unchained.modules.ShinyBooster

@Mod(UnchainedMod.MOD_ID)
object UnchainedMod {
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    const val MOD_ID = "unchained"

    @EventBusSubscriber
    object Registration {
        @SubscribeEvent
        fun onInitialize(e: ServerStartedEvent) {
            ShinyBooster.initialize()
            HiddenBooster.initialize()
            IvBooster.initialize()
        }
    }
}