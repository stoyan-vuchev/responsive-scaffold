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
 * 2023-08-14: Refactored code from Scaffold.kt to make ResponsiveScaffold.kt.
 *
 * Please note that these modifications are subject to the terms of the Apache License, Version 2.0.
 */

package com.stoyanvuchev.responsive_scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.offset

/**
 * A modified Material Design layout.
 *
 * Responsive Scaffold is a modified [Scaffold], implementing the basic material design visual layout structure.
 *
 * This component provides API to put together several material components to construct your
 * screen, by ensuring proper layout strategy for them and collecting necessary data so these
 * components will work together correctly on different window form factors.
 *
 * @param modifier a [Modifier] to be applied to the scaffold
 * @param topBar a top app bar component, typically a [TopAppBar]
 * @param bottomBar a bottom bar component, typically a [NavigationBar]
 * @param sideRail a side rail component, typically a [NavigationRail]
 * @param snackbarHost a component to host [Snackbar]s that are pushed to be shown via
 * [SnackbarHostState.showSnackbar], typically a [SnackbarHost]
 * @param floatingActionButton the main action button of the screen, typically a [FloatingActionButton].
 * It will be shown only if the window form factor is compact. For medium / expanded window from factor,
 * declare a [FloatingActionButton] into the header of a [NavigationRail].
 * @param floatingActionButtonPosition position of the FAB on the screen. See [FabPosition].
 * @param containerColor a color used for the background of the scaffold. Use [Color.Transparent]
 * to have no color.
 * @param contentColor a preferred color for the content inside the scaffold. Defaults to either the
 * matching content color for [containerColor], or to the current [LocalContentColor] if
 * [containerColor] is not a color from the theme.
 * @param contentWindowInsets a window insets to be passed to [content] slot via [PaddingValues]
 * params. Scaffold will take the insets into account from the top/bottom/start only if the [topBar]/
 * [bottomBar]/[sideRail] are not present, as the scaffold expect [topBar]/[bottomBar]/[sideRail]
 * to handle insets instead.
 * @param windowSizeClass used for under the hood calculations from a WindowSizeClass instance, with a support for optional overriding.
 * @param content the main content of the screen, e.g. a [LazyColumn]. The lambda receives a [PaddingValues] that should be
 * applied to the content root via [Modifier.padding] and [Modifier.consumeWindowInsets] to
 * properly offset top / bottom bars and side rail. If using [Modifier.verticalScroll], apply this modifier to
 * the child of the scroll, and not on the scroll itself.
 */
@Composable
fun ResponsiveScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    sideRail: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.BottomEnd,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    windowSizeClass: WindowSizeClass = LocalWindowSizeClass.current ?: calculateWindowSizeClass(),
    content: @Composable (PaddingValues) -> Unit
) {

    val isCompactWidth by rememberUpdatedState(windowSizeClass.isCompactWidth())

    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        content = {

            ResponsiveScaffoldLayout(
                fabPosition = floatingActionButtonPosition,
                topBar = topBar,
                bottomBar = if (isCompactWidth) bottomBar else ({}),
                sideRail = if (!isCompactWidth) sideRail else ({}),
                content = content,
                snackbar = snackbarHost,
                contentWindowInsets = contentWindowInsets,
                fab = if (isCompactWidth) floatingActionButton else ({})
            )

        }
    )

}

/**
 * Layout for a [ResponsiveScaffold]'s content.
 *
 * @param fabPosition [FabPosition] for the FAB (if present)
 * @param topBar the content to place at the top of the [ResponsiveScaffold], typically a [TopAppBar]
 * @param content the main 'body' of the [ResponsiveScaffold]
 * @param snackbar the [Snackbar] displayed on top of the [content]
 * @param fab the [FloatingActionButton] displayed on top of the [content], below the [snackbar]
 * and above the [bottomBar]
 * @param bottomBar the content to place at the bottom of the [ResponsiveScaffold], on top of the
 * [content], typically a [NavigationBar].
 * @param sideRail the content to place at the start of the [ResponsiveScaffold], typically a [NavigationRail].
 */
@Composable
private fun ResponsiveScaffoldLayout(
    fabPosition: FabPosition,
    topBar: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    snackbar: @Composable () -> Unit,
    fab: @Composable () -> Unit,
    contentWindowInsets: WindowInsets,
    bottomBar: @Composable () -> Unit,
    sideRail: @Composable () -> Unit
) {

    SubcomposeLayout { constraints ->

        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        layout(
            width = layoutWidth,
            height = layoutHeight
        ) {

            val fabPlaceables = subcompose(
                slotId = ResponsiveScaffoldLayoutContent.Fab,
                content = fab
            ).mapNotNull { measurable ->

                val leftInset = contentWindowInsets.getLeft(
                    density = this@SubcomposeLayout,
                    layoutDirection = layoutDirection
                )

                val rightInset = contentWindowInsets.getRight(
                    density = this@SubcomposeLayout,
                    layoutDirection = layoutDirection
                )

                val bottomInset = contentWindowInsets.getBottom(
                    density = this@SubcomposeLayout
                )

                measurable.measure(
                    constraints = looseConstraints.offset(
                        horizontal = (-leftInset) - rightInset,
                        vertical = (-bottomInset)
                    )
                ).takeIf { it.height != 0 && it.width != 0 }

            }

            val fabSpacing = FabSpacing.roundToPx()
            val fabPlacement = if (fabPlaceables.isNotEmpty()) {

                val fabWidth = fabPlaceables.maxByOrNull { it.width }!!.width
                val fabHeight = fabPlaceables.maxByOrNull { it.height }!!.height

                // FAB distance from the start of the layout, taking into account LTR / RTL
                val fabLeftOffset = if (fabPosition == FabPosition.BottomEnd) {
                    if (layoutDirection == LayoutDirection.Rtl) fabSpacing
                    else layoutWidth - fabWidth - fabSpacing
                } else (layoutWidth - fabWidth) / 2

                FabPlacement(
                    left = fabLeftOffset,
                    width = fabWidth,
                    height = fabHeight
                )

            } else null

            // Defining side rail placeables.
            val sideRailPlaceables = subcompose(
                slotId = ResponsiveScaffoldLayoutContent.SideRail,
                content = sideRail
            ).map { it.measure(looseConstraints) }

            val sideRailWidth = sideRailPlaceables.maxByOrNull { it.width }?.width

            // Defining top bar placeables & height.
            val topBarPlaceables = subcompose(
                slotId = ResponsiveScaffoldLayoutContent.TopBar,
                content = topBar
            ).map {

                val leftInset = if (
                    layoutDirection == LayoutDirection.Ltr
                    && sideRailWidth != null
                ) sideRailWidth else 0

                val rightInset = if (
                    layoutDirection == LayoutDirection.Rtl
                    && sideRailWidth != null
                ) sideRailWidth else 0

                it.measure(
                    constraints = looseConstraints.offset(
                        horizontal = (-leftInset) - rightInset,
                    )
                )

            }
            val topBarHeight = topBarPlaceables.maxByOrNull { it.height }?.height ?: 0

            // Defining snackbar placeables, width & height.
            val snackbarPlaceables = subcompose(
                slotId = ResponsiveScaffoldLayoutContent.Snackbar,
                content = snackbar
            ).map {

                val leftInset = sideRailWidth ?: contentWindowInsets.getLeft(
                    density = this@SubcomposeLayout,
                    layoutDirection = layoutDirection
                )

                val rightInset = contentWindowInsets.getRight(
                    density = this@SubcomposeLayout,
                    layoutDirection = layoutDirection
                )

                val bottomInset = contentWindowInsets.getBottom(
                    density = this@SubcomposeLayout
                )

                it.measure(
                    constraints = looseConstraints.offset(
                        horizontal = (-leftInset) - rightInset,
                        vertical = (-bottomInset)
                    )
                )

            }

            val snackbarHeight = snackbarPlaceables.maxByOrNull { it.height }?.height ?: 0
            val snackbarWidth = (snackbarPlaceables.maxByOrNull { it.width }?.width ?: 0) -
                    (sideRailWidth ?: 0)

            // Defining bottom bar placeables & height.
            val bottomBarPlaceables = subcompose(
                slotId = ResponsiveScaffoldLayoutContent.BottomBar,
                content = {

                    CompositionLocalProvider(
                        LocalFabPlacement provides fabPlacement,
                        content = bottomBar
                    )

                }
            ).map { it.measure(looseConstraints) }
            val bottomBarHeight = bottomBarPlaceables.maxByOrNull { it.height }?.height

            // FAB bottom offset.
            val fabOffsetFromBottom = fabPlacement?.let {
                if (bottomBarHeight != null) bottomBarHeight + it.height + fabSpacing
                else it.height + fabSpacing +
                        contentWindowInsets.getBottom(density = this@SubcomposeLayout)
            }

            // Snackbar bottom offset.
            val snackbarOffsetFromBottom = if (snackbarHeight != 0) {
                snackbarHeight +
                        (fabOffsetFromBottom ?: bottomBarHeight
                        ?: contentWindowInsets.getBottom(this@SubcomposeLayout))
            } else 0

            // Defining body content placeables.
            val bodyContentPlaceables = subcompose(ResponsiveScaffoldLayoutContent.MainContent) {
                val insets = contentWindowInsets.asPaddingValues(this@SubcomposeLayout)
                val innerPadding = PaddingValues(
                    top = if (topBarPlaceables.isEmpty()) insets.calculateTopPadding()
                    else topBarHeight.toDp(),
                    bottom = if (bottomBarPlaceables.isEmpty() || bottomBarHeight == null) {
                        insets.calculateBottomPadding()
                    } else bottomBarHeight.toDp(),
                    start = if (sideRailPlaceables.isEmpty() || sideRailWidth == null) {
                        insets.calculateStartPadding((this@SubcomposeLayout).layoutDirection)
                    } else sideRailWidth.toDp(),
                    end = insets.calculateEndPadding((this@SubcomposeLayout).layoutDirection)
                )
                content(innerPadding)
            }.map { it.measure(looseConstraints) }

            /*
             * Defining each content placeables position offset.
             */

            // Defining the top bar placeables position offset.
            val topBarPlaceablesOffset = IntOffset(
                x = if (layoutDirection == LayoutDirection.Ltr) sideRailWidth ?: 0 else 0,
                y = 0
            )

            // Defining the snackbar placeables position offset.
            val snackbarPlaceablesPositionOffset = IntOffset(
                x = (layoutWidth - snackbarWidth) / 2 + contentWindowInsets.getLeft(
                    density = this@SubcomposeLayout,
                    layoutDirection = layoutDirection
                ),
                y = layoutHeight - snackbarOffsetFromBottom
            )

            // Defining the bottom bar placeables position offset.
            val bottomBarPlaceablesPositionOffset = IntOffset(
                x = 0,
                y = layoutHeight - (bottomBarHeight ?: 0)
            )

            // Defining the side rail placeables position offset.
            val sideRailPlaceablesPositionOffset = IntOffset(
                x = if (layoutDirection == LayoutDirection.Ltr) 0
                else layoutWidth - (sideRailWidth ?: 0),
                y = 0
            )

            /*
             * Placing the placeables by maintaining the layout hierarchy:
             * MainContent >> TopBar >> Snackbar >> BottomBar >> SideRail >> Fab
             */

            // Placing the body content placeables.
            bodyContentPlaceables.forEach { it.place(position = IntOffset.Zero) }

            // Placing the top bar placeables.
            topBarPlaceables.forEach { it.place(position = topBarPlaceablesOffset) }

            // Placing the snackbar placeables.
            snackbarPlaceables.forEach { it.place(position = snackbarPlaceablesPositionOffset) }

            // Placing the bottom bar placeables.
            bottomBarPlaceables.forEach { it.place(position = bottomBarPlaceablesPositionOffset) }

            // Placing the side rail placeables.
            sideRailPlaceables.forEach { it.place(position = sideRailPlaceablesPositionOffset) }

            // Explicitly not using placeRelative here as `leftOffset` already accounts for RTL
            fabPlacement?.let { placement ->
                fabPlaceables.forEach {
                    it.place(
                        x = placement.left,
                        y = layoutHeight - (fabOffsetFromBottom ?: 0)
                    )
                }
            }

        }

    }

}

/** Constants defining [ResponsiveScaffold]'s layout content slots. */
@Immutable
private enum class ResponsiveScaffoldLayoutContent {
    MainContent, TopBar, Snackbar, BottomBar, SideRail, Fab
}