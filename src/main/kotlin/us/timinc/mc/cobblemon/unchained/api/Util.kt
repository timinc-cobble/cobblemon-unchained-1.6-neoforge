package us.timinc.mc.cobblemon.unchained.api

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon

object Util {
    fun matchesList(pokemon: Pokemon, whitelist: Set<String>, blacklist: Set<String>): Boolean {
        return when {
            whitelist.isNotEmpty() && blacklist.isNotEmpty() -> whitelist.any {
                pokemonHasLabel(
                    pokemon, it
                )
            } || blacklist.none { pokemonHasLabel(pokemon, it) }

            whitelist.isNotEmpty() -> whitelist.any { pokemonHasLabel(pokemon, it) }
            blacklist.isNotEmpty() -> blacklist.none { pokemonHasLabel(pokemon, it) }
            else -> true
        }
    }

    fun pokemonHasLabel(pokemon: Pokemon, label: String): Boolean {
        return pokemon.hasLabels(label) || pokemon.species.resourceIdentifier.toString() == label
    }

    fun matchesList(
        properties: PokemonProperties,
        whitelist: MutableSet<String>,
        blacklist: MutableSet<String>,
    ): Boolean = matchesList(properties.create(), whitelist, blacklist)
}