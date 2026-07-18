package com.example.bikeexpensetracker.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable

val enterTransition = fadeIn(
    animationSpec = tween(300)
) + slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(300)
)

val exitTransition = fadeOut(
    animationSpec = tween(300)
) + slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(300)
)

val popEnterTransition = fadeIn(
    animationSpec = tween(300)
) + slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(300)
)

val popExitTransition = fadeOut(
    animationSpec = tween(300)
) + slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(300)
)