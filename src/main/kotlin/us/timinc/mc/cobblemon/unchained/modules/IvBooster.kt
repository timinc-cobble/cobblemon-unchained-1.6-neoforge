package us.timinc.mc.cobblemon.unchained.modules

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.spawning.BestSpawner.fishingSpawner
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawnerFactory
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.pokemon.IVs
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.unchained.api.AbstractActionInfluenceBooster
import us.timinc.mc.cobblemon.unchained.api.AbstractBoostConfig
import us.timinc.mc.cobblemon.unchained.api.AbstractBooster
import kotlin.math.min

object IvBooster : AbstractBooster<IvBoosterConfig>(
    "ivBooster",
    IvBoosterConfig::class.java
) {
    override fun subInit() {
        PlayerSpawnerFactory.influenceBuilders.add { IvBoosterInfluence(config, ::debug, it) }
        PlatformEvents.SERVER_STARTED.subscribe(Priority.LOWEST) { _ ->
            fishingSpawner.influences.add(IvBoosterInfluence(config, ::debug))
        }
    }
}

class IvBoosterInfluence(
    override val config: IvBoosterConfig,
    override val debug: (String) -> Unit,
    override val player: ServerPlayer? = null,
) : AbstractActionInfluenceBooster(config, debug, player) {
    override fun boostAction(
        action: PokemonSpawnAction,
        pokemon: Pokemon,
        species: ResourceLocation,
        form: String,
        points: Float,
        player: ServerPlayer,
    ) {
        val intPoints = points.toInt()
        debug("${player.name.string} wins with $intPoints points, $intPoints perfect IVs")
        if (intPoints <= 0) {
            debug("conclusion: player didn't get any perfect IVs")
            return
        }

        action.props.ivs
        if (action.props.ivs == null) {
            action.props.ivs = IVs()
        }

        val madePerfect: MutableSet<Stat> = mutableSetOf()
        val remainingStats = Stats.PERMANENT.toMutableSet()

        for (stat in Stats.PERMANENT) {
            if (action.props.ivs!![stat] == IVs.MAX_VALUE) {
                madePerfect.add(stat)
                remainingStats.remove(stat)
            }
        }

        while (madePerfect.size < min(intPoints, Stats.PERMANENT.size)) {
            val taken = remainingStats.random()
            madePerfect.add(taken)
            remainingStats.remove(taken)
            action.props.ivs!![taken] = IVs.MAX_VALUE
        }

        debug("conclusion: set $intPoints IVs to perfect $madePerfect")
    }
}

class IvBoosterConfig : AbstractBoostConfig() {
    override val koStreakPoints = 0F
    override val koCountPoints = 0F
    override val captureStreakPoints = 1F
    override val captureCountPoints = 0F
    override val thresholds: Map<Int, Float> = mutableMapOf(Pair(5, 1F), Pair(10, 2F), Pair(20, 3F), Pair(30, 4F))
}