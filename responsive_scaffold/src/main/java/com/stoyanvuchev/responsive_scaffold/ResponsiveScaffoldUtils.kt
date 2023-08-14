/*
 * Copyright 2021 The Android Open Source Project
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
 *
 * Please note that these modifications are subject to the terms of the Apache License, Version 2.0.
 */

package com.stoyanvuchev.responsive_scaffold

import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/** Useful utilities for a [ResponsiveScaffold]. */
object ResponsiveScaffoldUtils {

    /** Returns responsive [WindowInsets] values for a [TopAppBar].  */
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    fun topAppBarWindowInsets(): WindowInsets {

        val topAppBarWindowInsets = TopAppBarDefaults.windowInsets
        val windowSizeClass = calculateWindowSizeClass(LocalContext.current as Activity)

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