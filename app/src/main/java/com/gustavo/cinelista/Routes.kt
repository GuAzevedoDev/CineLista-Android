package com.gustavo.cinelista

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable data object Splash : Route
    @Serializable data object Login : Route
    @Serializable data object Home : Route
    @Serializable data object Movies : Route
    @Serializable data object Profile : Route
    @Serializable data class Detail(val movieId: Int) : Route
}
