/*
 * Copyright 2021-2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Modifications by Stoyan Vuchev:
 *
 * 2023-08-14: Refactored code from TopAppBarDefaults.kt to make topAppBarWindowInsets() method.
 * 2023-08-14: Refactored code from Scaffold.kt used for the FloatingActionButton logic.
 * 2023-08-29: Refactored code from AndroidWindowSizeClass.android.kt used for a modified calculateWindowSizeClass method.
 *
 * Please note that these modifications are subject to the terms of the Apache License, Version 2.0.
 */

package com.stoyanvuchev.responsive_scaffold

import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/** Useful utilities for a [ResponsiveScaffold]. */
object ResponsiveScaffoldUtils {

    /** Returns responsive [WindowInsets] values for a [TopAppBar].  */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun topAppBarWindowInsets(): WindowInsets {

        val topAppBarWindowInsets = TopAppBarDefaults.windowInsets
        val windowSizeClass = LocalWindowSizeClass.current ?: calculateWindowSizeClass()

        // If the window width size is compact, return the default top app bar window insets,
        // otherwise, return only the top and end sides of the top app bar window insets.
        return if (windowSizeClass.isCompactWidth()) topAppBarWindowInsets
        else topAppBarWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.End)

    }

}

/**
 * The possible positions for a [FloatingActionButton] attached to a [ResponsiveScaffold].
 */
@JvmInline
value class FabPosition internal constructor(
    @Suppress("unused")
    private val value: Int
) {

    companion object {

        /**
         * Position FAB at the bottom center of the screen, above the [NavigationBar] (if it
         * exists).
         */
        val BottomCenter = FabPosition(0)

        /**
         * Position FAB at the bottom end of the screen, above the [NavigationBar] (if it
         * exists).
         */
        val BottomEnd = FabPosition(1)

    }

    override fun toString(): String {
        return when (this) {
            BottomCenter -> "FabPosition.BottomCenter"
            else -> "FabPosition.BottomEnd"
        }
    }

}

/**
 * Placement information for a [FloatingActionButton] inside a [ResponsiveScaffold].
 *
 * @property left the FAB's offset from the left edge of the bottom bar, already adjusted for RTL
 * support
 * @property width the width of the FAB
 * @property height the height of the FAB
 */
@Immutable
internal class FabPlacement(
    val left: Int,
    val width: Int,
    val height: Int
)

/**
 * CompositionLocal containing a [FabPlacement] that is used to calculate the FAB bottom offset.
 */
internal val LocalFabPlacement = staticCompositionLocalOf<FabPlacement?> { null }

/**
 * FAB spacing above the bottom bar, side rail / bottom of the [ResponsiveScaffold].
 **/
internal val FabSpacing = 16.dp

/** Composition Local key used for passing a single instance of [WindowSizeClass]. */
val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass?> { null }

/**
 * A top level composable providing a [WindowSizeClass] instance.
 * To consume it, use [LocalWindowSizeClass].
 *
 * @param windowSizeClass the [WindowSizeClass] instance.
 * @param content the composition content.
 **/
@Composable
fun ProvideWindowSizeClass(
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(),
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    LocalWindowSizeClass provides windowSizeClass,
    content = content
)

/**
 * A modified method to calculate [WindowSizeClass] inside a composable
 * without the need of an [Activity] parameter.
 **/
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun calculateWindowSizeClass(): WindowSizeClass {

    // Observe view configuration changes and recalculate the size class on each change. We can't
    // use Activity#onConfigurationChanged as this will sometimes fail to be called on different
    // API levels, hence why this function needs to be @Composable so we can observe the
    // ComposeView's configuration changes.

    val configuration = LocalConfiguration.current
    val layoutDirection = LocalLayoutDirection.current
    val windowInsets = WindowInsets.safeContent

    val width: Dp
    val height: Dp

    LocalDensity.current.run {

        width = configuration.screenWidthDp.dp +
                windowInsets.getLeft(this, layoutDirection).toDp() +
                windowInsets.getRight(this, layoutDirection).toDp()

        height = configuration.screenHeightDp.dp +
                windowInsets.getTop(this).toDp() +
                windowInsets.getBottom(this).toDp()

    }

    // Return the calculated [WindowSizeClass].
    return WindowSizeClass.calculateFromSize(DpSize(width, height))

}