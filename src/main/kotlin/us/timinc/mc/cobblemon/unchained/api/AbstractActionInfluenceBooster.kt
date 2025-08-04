package us.timinc.mc.cobblemon.unchained.api

import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

abstract class AbstractActionInfluenceBooster(
    open val config: AbstractBoostConfig,
    open val debug: (String) -> Unit,
    open val player: ServerPlayer? = null,
) : SpawningInfluence {
    override fun affectAction(action: SpawnAction<*>) {
        if (action !is PokemonSpawnAction) return
        val player = player ?: action.ctx.cause.entity as? ServerPlayer ?: return
        val pokemon = action.props.create()
        val species = pokemon.species.resourceIdentifier
        val form = pokemon.form.name
        debug("$species|$form spawning at ${action.ctx.position.toShortString()} on ${player.name.string}")

        if (!Util.matchesList(pokemon, config.whitelist, config.blacklist)) {
            debug("$species is blocked by the blacklist/whitelist")
            return
        }

        val points = config.getPointsFromThreshold(player, species, form)

        boostAction(action, pokemon, species, form, points, player)
    }

    abstract fun boostAction(
        action: PokemonSpawnAction,
        pokemon: Pokemon,
        species: ResourceLocation,
        form: String,
        points: Float,
        player: ServerPlayer,
    )
}