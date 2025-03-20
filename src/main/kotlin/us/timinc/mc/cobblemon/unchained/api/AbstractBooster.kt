package us.timinc.mc.cobblemon.unchained.api

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import us.timinc.mc.cobblemon.unchained.UnchainedMod.MOD_ID
import us.timinc.mc.cobblemon.unchained.config.ConfigBuilder

abstract class AbstractBooster<T : AbstractBoostConfig>(
    val name: String,
    val configClass: Class<T>
) {
    val logger: Logger = LogManager.getLogger("$MOD_ID/$name")
    lateinit var config: T

    fun initialize() {
        config = ConfigBuilder.load(configClass, "cobblemon/unchained/$name")

        subInit()
    }

    abstract fun subInit()

    fun debug(message: String) {
        if (!config.debug) return
        logger.info(message)
    }
}
