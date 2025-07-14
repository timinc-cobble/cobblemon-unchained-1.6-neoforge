package us.timinc.mc.cobblemon.unchained.modules

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.spawning.BestSpawner.fishingSpawner
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawnerFactory
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.unchained.api.AbstractBoostConfig
import us.timinc.mc.cobblemon.unchained.api.AbstractBooster
import us.timinc.mc.cobblemon.unchained.api.AbstractWeightInfluenceBooster

object SpawnChainer : AbstractBooster<SpawnChainerConfig>(
    "spawnChainer",
    SpawnChainerConfig::class.java
) {
    override fun subInit() {
        PlayerSpawnerFactory.influenceBuilders.add { SpawnChainerInfluence(config, ::debug) }
        PlatformEvents.SERVER_STARTED.subscribe(Priority.LOWEST) { _ ->
            fishingSpawner.influences.add(SpawnChainerInfluence(config, ::debug))
        }
    }
}

class SpawnChainerInfluence(
    val config: SpawnChainerConfig,
    val debug: (String) -> Unit,
) : AbstractWeightInfluenceBooster() {
    override fun boostWeight(detail: PokemonSpawnDetail, player: ServerPlayer, weight: Float): Float {
        val species = detail.pokemon.species?.asIdentifierDefaultingNamespace() ?: return weight
        val form = detail.pokemon.form ?: "Normal"

        val multiplier = config.getPointsFromThreshold(player, species, form)

        if (multiplier != 1.0) {
            debug("${detail.pokemon.originalString} spawn boosted by $multiplier for ${player.name.string}")
        }
        return (weight * multiplier).toFloat()
    }
}

class SpawnChainerConfig : AbstractBoostConfig(1.0) {
    override val koStreakPoints: Int = 1
    override val koCountPoints: Int = 0
    override val captureStreakPoints: Int = 0
    override val captureCountPoints: Int = 0
    override val thresholds: Map<Int, Double> = mutableMapOf(
        5 to 1.2,
        10 to 1.5,
        20 to 2.0,
        30 to 2.5,
        50 to 3.0,
        75 to 3.5,
        100 to 4.0,
    )
}