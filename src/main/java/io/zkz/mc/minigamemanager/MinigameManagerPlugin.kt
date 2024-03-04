package io.zkz.mc.minigamemanager

import io.zkz.mc.gametools.GTPlugin
import io.zkz.mc.gametools.resourcepack.IProvidesResourcePackParts
import io.zkz.mc.gametools.resourcepack.ResourcePackBuilder

class MinigameManagerPlugin : GTPlugin<MinigameManagerPlugin>(), IProvidesResourcePackParts {
    override fun buildResourcePack(builder: ResourcePackBuilder) {
        builder.apply {
            withNegativeSpaceCharacters()
        }
    }
}
