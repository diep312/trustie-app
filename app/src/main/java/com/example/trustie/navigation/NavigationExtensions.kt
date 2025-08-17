package com.example.trustie.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

object NavigationManager {
    private var lastNavigationTime = 0L
    private const val NAVIGATION_THROTTLE_DURATION = 500L

    fun NavHostController.safeNavigate(
        route: String,
        builder: (NavOptionsBuilder.() -> Unit)? = null
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastNavigationTime > NAVIGATION_THROTTLE_DURATION) {
            lastNavigationTime = currentTime

            currentDestination?.route?.let { currentRoute ->
                if (currentRoute != route) {
                    if (builder != null) {
                        navigate(route, builder)
                    } else {
                        navigate(route)
                    }
                }
            }
        }
    }

    fun NavHostController.safePopBackStack(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastNavigationTime > NAVIGATION_THROTTLE_DURATION) {
            lastNavigationTime = currentTime

            return if (previousBackStackEntry != null) {
                popBackStack()
            } else {
                false
            }
        }
        return false
    }
}