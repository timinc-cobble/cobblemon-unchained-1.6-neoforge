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

        if (multiplier != 1F) {
            debug("${detail.pokemon.originalString} spawn boosted by $multiplier for ${player.name.string}")
        }
        return (weight * multiplier).toFloat()
    }
}

class SpawnChainerConfig : AbstractBoostConfig(1F) {
    override val koStreakPoints = 1F
    override val koCountPoints = 0F
    override val captureStreakPoints = 0F
    override val captureCountPoints = 0F
    override val thresholds = mutableMapOf(
        5 to 1.2F,
        10 to 1.5F,
        20 to 2.0F,
        30 to 2.5F,
        50 to 3.0F,
        75 to 3.5F,
        100 to 4.0F,
    )
}