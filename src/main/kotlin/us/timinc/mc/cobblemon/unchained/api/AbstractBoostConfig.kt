@file:Suppress("MemberVisibilityCanBePrivate")

package us.timinc.mc.cobblemon.unchained.api

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.counter.extensions.getCounterManager
import us.timinc.mc.cobblemon.counter.registry.CounterTypes.CAPTURE
import us.timinc.mc.cobblemon.counter.registry.CounterTypes.KO

abstract class AbstractBoostConfig(val defaultValue: Float = 0F) {
    val debug: Boolean = false
    val blacklist = mutableSetOf<String>()
    val whitelist = mutableSetOf<String>()
    val careAboutForms: Boolean = true
    abstract val koStreakPoints: Float
    abstract val koCountPoints: Float
    abstract val captureStreakPoints: Float
    abstract val captureCountPoints: Float
    abstract val thresholds: Map<Int, Float>

    fun getPointsFromThreshold(player: ServerPlayer, species: ResourceLocation, form: String): Float {
        val calcForm = if (careAboutForms) form else null
        val counterManager = player.getCounterManager()

        val koStreak = counterManager.getStreakScore(KO, species, calcForm)
        val koCount = counterManager.getCountScore(KO, species, calcForm)

        val captureStreak = counterManager.getStreakScore(CAPTURE, species, calcForm)
        val captureCount = counterManager.getCountScore(CAPTURE, species, calcForm)

        val points =
            (koStreak * koStreakPoints) + (koCount * koCountPoints) + (captureStreak * captureStreakPoints) + (captureCount * captureCountPoints)

        return thresholds.maxOfOrNull { if (it.key <= points) it.value else defaultValue } ?: defaultValue
    }

    fun getPointsFromThreshold(player: ServerPlayer, pokemon: Pokemon): Float {
        if (!Util.matchesList(pokemon, whitelist, blacklist)) return defaultValue
        return getPointsFromThreshold(player, pokemon.species.resourceIdentifier, pokemon.form.name)
    }
}