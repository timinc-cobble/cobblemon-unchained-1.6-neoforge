package us.timinc.mc.cobblemon.unchained.modules

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawnerFactory
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.unchained.api.AbstractBoostConfig
import us.timinc.mc.cobblemon.unchained.api.AbstractBooster
import us.timinc.mc.cobblemon.unchained.api.AbstractInfluenceBooster
import kotlin.random.Random.Default.nextInt

object HiddenBooster : AbstractBooster<HiddenBoosterConfig>(
    "hiddenBooster",
    HiddenBoosterConfig::class.java
) {
    override fun subInit() {
        PlayerSpawnerFactory.influenceBuilders.add { HiddenBoosterInfluence(it, config, ::debug) }
    }
}

class HiddenBoosterInfluence(
    override val player: ServerPlayer,
    override val config: HiddenBoosterConfig,
    override val debug: (String) -> Unit,
) :
    AbstractInfluenceBooster(player, config, debug) {
    override fun boost(
        action: PokemonSpawnAction,
        pokemon: Pokemon,
        species: ResourceLocation,
        form: String,
        points: Int,
    ) {
        val totalMarbles = config.marbles
        val ability = pokemon.form.abilities.mapping[Priority.LOW]?.random()?.template?.name

        if (ability == null) {
            debug("conclusion: species doesn't have hidden ability")
            return
        }

        if (points == 0) {
            debug("conclusion: player hasn't unlocked hidden ability chance")
            return
        }

        val hiddenAbilityRoll = nextInt(0, totalMarbles)
        val successfulRoll = hiddenAbilityRoll < points

        debug(
            "${player.name.string} has $points points, has a $points out of ${totalMarbles}, rolls a $hiddenAbilityRoll, ${if (successfulRoll) "wins" else "loses"}"
        )

        if (!successfulRoll) {
            debug("conclusion: did not give $species its hidden ability")
            return
        }

        action.props.ability = ability
        debug("conclusion: gave $species its hidden ability")
    }
}

class HiddenBoosterConfig : AbstractBoostConfig() {
    override val koStreakPoints = 100
    override val koCountPoints = 1
    override val captureStreakPoints = 0
    override val captureCountPoints = 0
    override val thresholds: Map<Int, Int> = mutableMapOf(99 to 1)
    val marbles = 5
}