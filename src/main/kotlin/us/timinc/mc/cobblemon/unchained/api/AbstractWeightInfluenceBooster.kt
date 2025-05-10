package us.timinc.mc.cobblemon.unchained.api

import com.cobblemon.mod.common.api.spawning.context.SpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import net.minecraft.server.level.ServerPlayer

abstract class AbstractWeightInfluenceBooster : SpawningInfluence {
    override fun affectWeight(detail: SpawnDetail, ctx: SpawningContext, weight: Float): Float {
        val player = ctx.cause.entity as? ServerPlayer
        if (detail !is PokemonSpawnDetail || player === null) return super.affectWeight(detail, ctx, weight)
        return super.affectWeight(detail, ctx, boostWeight(detail, player, weight))
    }

    abstract fun boostWeight(detail: PokemonSpawnDetail, player: ServerPlayer, weight: Float): Float
}