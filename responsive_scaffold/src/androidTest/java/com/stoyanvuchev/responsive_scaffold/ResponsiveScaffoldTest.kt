package com.stoyanvuchev.responsive_scaffold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.size
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ResponsiveScaffoldTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val topAppBarTitle = "Responsive Scaffold"
    private val snackbarMsg = "Hey, I'm here!"

    @OptIn(ExperimentalMaterial3Api::class)
    @Before
    fun setUp() {
        composeRule.setContent {
            ProvideWindowSizeClass {

                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                ResponsiveScaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TopAppBar(
                            title = { Text(text = topAppBarTitle) },
                            scrollBehavior = scrollBehavior,
                            windowInsets = ResponsiveScaffoldUtils.topAppBarWindowInsets()
                        )
                    },
                    bottomBar = {
                        NavigationBar(modifier = Modifier.testTag("bottomBar")) {}
                    },
                    sideRail = {
                        NavigationRail(modifier = Modifier.testTag("sideRail"),
                            header = {
                                FloatingActionButton(
                                    modifier = Modifier.testTag("sideRailFAB"),
                                    onClick = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = snackbarMsg
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = "Add"
                                    )
                                }
                            }
                        ) {}
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    floatingActionButton = {
                        FloatingActionButton(
                            modifier = Modifier.testTag("floatingActionButton"),
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(message = snackbarMsg)
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add")
                        }
                    }
                ) { paddingValues ->

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = paddingValues,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        content = { items(100) { Text(text = "Item: $it") } }
                    )

                }

            }
        }
    }

    @Test
    fun assertThatTheTopAppBarIsDisplayed() = runTest {
        composeRule.awaitIdle()
        composeRule.onNodeWithText(topAppBarTitle).assertIsDisplayed()
    }

    @Test
    fun assertThatTheBottomBarIsDisplayed() = runTest {
        composeRule.awaitIdle()
        val bounds = composeRule.onRoot().getBoundsInRoot().size
        if (bounds.width < 600.dp) {
            composeRule.onNodeWithTag("bottomBar").assertIsDisplayed()
        } else {
            composeRule.onNodeWithTag("bottomBar").assertDoesNotExist()
        }
    }

    @Test
    fun assertThatTheSideRailIsDisplayed() = runTest {
        composeRule.awaitIdle()
        val bounds = composeRule.onRoot().getBoundsInRoot().size
        if (bounds.width < 600.dp) {
            composeRule.onNodeWithTag("sideRail").assertDoesNotExist()
        } else {
            composeRule.onNodeWithTag("sideRail").assertIsDisplayed()
        }
    }

    @Test
    fun assertThatTheFloatingActionButtonIsDisplayed() = runTest {
        composeRule.awaitIdle()
        val bounds = composeRule.onRoot().getBoundsInRoot().size
        if (bounds.width < 600.dp) {
            composeRule.onNodeWithTag("floatingActionButton").assertIsDisplayed()
        } else {
            composeRule.onNodeWithTag("floatingActionButton").assertDoesNotExist()
        }
    }

    @Test
    fun assertThatTheSnackbarHostIsDisplayed() = runTest {
        composeRule.awaitIdle()
        val bounds = composeRule.onRoot().getBoundsInRoot().size
        if (bounds.width < 600.dp) {
            composeRule.onNodeWithTag("floatingActionButton").performClick()
        } else {
            composeRule.onNodeWithTag("sideRailFAB").performClick()
        }
        composeRule.awaitIdle()
        composeRule.onNodeWithText(snackbarMsg).assertIsDisplayed()
    }

    @Test
    fun assertThatTheMainContentIsDisplayed() = runTest {
        composeRule.awaitIdle()
        composeRule.onNodeWithText("Item: 1").assertIsDisplayed()
    }

}