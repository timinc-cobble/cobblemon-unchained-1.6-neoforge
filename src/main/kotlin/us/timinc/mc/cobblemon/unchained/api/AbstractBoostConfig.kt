@file:Suppress("MemberVisibilityCanBePrivate")

package us.timinc.mc.cobblemon.unchained.api

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.counter.api.CounterType
import us.timinc.mc.cobblemon.counter.extensions.getCounterManager

abstract class AbstractBoostConfig {
    val debug: Boolean = false
    val blacklist = mutableSetOf<String>()
    val whitelist = mutableSetOf<String>()
    val careAboutForms: Boolean = true
    abstract val koStreakPoints: Int
    abstract val koCountPoints: Int
    abstract val captureStreakPoints: Int
    abstract val captureCountPoints: Int
    abstract val thresholds: Map<Int, Int>

    fun getPointsFromThreshold(player: ServerPlayer, species: ResourceLocation, form: String): Int {
        val calcForm = if (careAboutForms) form else null
        val counterManager = player.getCounterManager()

        val koStreak = counterManager.getStreakCount(CounterType.KO, species, calcForm)
        val koCount = counterManager.getCount(CounterType.KO, species, calcForm)

        val captureStreak = counterManager.getStreakCount(CounterType.CAPTURE, species, calcForm)
        val captureCount = counterManager.getCount(CounterType.CAPTURE, species, calcForm)

        val points =
            (koStreak * koStreakPoints) + (koCount * koCountPoints) + (captureStreak * captureStreakPoints) + (captureCount * captureCountPoints)

        return thresholds.maxOfOrNull { if (it.key <= points) it.value else 0 } ?: 0
    }

    fun getPointsFromThreshold(player: ServerPlayer, pokemon: Pokemon): Int {
        if (!Util.matchesList(pokemon, whitelist, blacklist)) return 0
        return getPointsFromThreshold(player, pokemon.species.resourceIdentifier, pokemon.form.name)
    }
}