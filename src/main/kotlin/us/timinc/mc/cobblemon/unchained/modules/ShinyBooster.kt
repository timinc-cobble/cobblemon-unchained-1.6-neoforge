package us.timinc.mc.cobblemon.unchained.modules

import com.cobblemon.mod.common.api.events.CobblemonEvents
import us.timinc.mc.cobblemon.unchained.api.AbstractBoostConfig
import us.timinc.mc.cobblemon.unchained.api.AbstractBooster

object ShinyBooster : AbstractBooster<ShinyBoostConfig>(
    "shinyBooster",
    ShinyBoostConfig::class.java
) {
    override fun subInit() {
        CobblemonEvents.SHINY_CHANCE_CALCULATION.subscribe { evt ->
            evt.addModificationFunction { currentRate, player, pokemon ->
                if (player === null) return@addModificationFunction currentRate
                val boost = config.getPointsFromThreshold(player, pokemon)
                val newRate = currentRate / (boost + 1)
                debug("A ${pokemon.species.name}|${pokemon.form.name} has spawned on ${player.name.string} and received a boost of $boost to change the rate to $newRate.")
                newRate.toFloat()
            }
        }
    }
}

class ShinyBoostConfig : AbstractBoostConfig(1.0) {
    override val koStreakPoints = 1
    override val koCountPoints = 0
    override val captureStreakPoints = 0
    override val captureCountPoints = 0
    override val thresholds: Map<Int, Double> = mutableMapOf(Pair(100, 1.0), Pair(300, 2.0), Pair(500, 3.0))
}