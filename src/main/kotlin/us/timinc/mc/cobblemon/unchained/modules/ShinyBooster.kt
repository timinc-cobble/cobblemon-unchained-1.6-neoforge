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
                newRate
            }
        }
    }
}

class ShinyBoostConfig : AbstractBoostConfig() {
    override val koStreakPoints = 1F
    override val koCountPoints = 0F
    override val captureStreakPoints = 0F
    override val captureCountPoints = 0F
    override val thresholds: Map<Int, Float> = mutableMapOf(Pair(100, 1F), Pair(300, 2F), Pair(500, 3F))
}