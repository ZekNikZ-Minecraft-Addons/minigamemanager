package io.zkz.mc.minigamemanager.state

import net.kyori.adventure.text.Component

interface IHasMapInfo {
    val currentMapName: Component
    val currentMapAuthor: Component?
}