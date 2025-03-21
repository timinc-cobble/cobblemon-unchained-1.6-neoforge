package us.timinc.mc.cobblemon.unchained.api

import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

abstract class AbstractInfluenceBooster(
    open val player: ServerPlayer,
    open val config: AbstractBoostConfig,
    open val debug: (String) -> Unit,
) : SpawningInfluence {
    override fun affectAction(action: SpawnAction<*>) {
        if (action !is PokemonSpawnAction) return
        val pokemon = action.props.create()
        val species = pokemon.species.resourceIdentifier
        val form = pokemon.form.name
        debug("$species|$form spawning at ${action.ctx.position.toShortString()} on ${player.name.string}")

        if (!Util.matchesList(pokemon, config.whitelist, config.blacklist)) {
            debug("$species is blocked by the blacklist/whitelist")
            return
        }

        val points = config.getPointsFromThreshold(player, species, form)

        boost(action, pokemon, species, form, points)
    }

    abstract fun boost(
        action: PokemonSpawnAction,
        pokemon: Pokemon,
        species: ResourceLocation,
        form: String,
        points: Int,
    )
}