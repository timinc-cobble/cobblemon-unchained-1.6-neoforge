@file:Suppress("MemberVisibilityCanBePrivate")

package us.timinc.mc.cobblemon.unchained.api

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.counter.extensions.getCounterManager
import us.timinc.mc.cobblemon.counter.registry.CounterTypes.CAPTURE
import us.timinc.mc.cobblemon.counter.registry.CounterTypes.KO

abstract class AbstractBoostConfig(val defaultValue: Double = 0.0) {
    val debug: Boolean = false
    val blacklist = mutableSetOf<String>()
    val whitelist = mutableSetOf<String>()
    val careAboutForms: Boolean = true
    abstract val koStreakPoints: Int
    abstract val koCountPoints: Int
    abstract val captureStreakPoints: Int
    abstract val captureCountPoints: Int
    abstract val thresholds: Map<Int, Double>

    fun getPointsFromThreshold(player: ServerPlayer, species: ResourceLocation, form: String): Double {
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

    fun getPointsFromThreshold(player: ServerPlayer, pokemon: Pokemon): Double {
        if (!Util.matchesList(pokemon, whitelist, blacklist)) return 0.0
        return getPointsFromThreshold(player, pokemon.species.resourceIdentifier, pokemon.form.name)
    }
}