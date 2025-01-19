package com.stoyanvuchev.responsivescaffold

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.stoyanvuchev.responsive_scaffold.FabPosition
import com.stoyanvuchev.responsive_scaffold.ProvideWindowSizeClass
import com.stoyanvuchev.responsive_scaffold.ResponsiveScaffold
import com.stoyanvuchev.responsive_scaffold.ResponsiveScaffoldUtils
import com.stoyanvuchev.responsivescaffold.ui.theme.ResponsiveScaffoldTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables Edge-To-Edge (Displaying content under Status & Navigation bars).
        enableEdgeToEdge()

        setContent {
            ResponsiveScaffoldTheme {
                ProvideWindowSizeClass {

                    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                    var selectedItemIndex by remember { mutableIntStateOf(0) }
                    val lazyListState = rememberLazyListState()
                    val snackbarHost = remember { SnackbarHostState() }
                    val scope = rememberCoroutineScope()

                    val onFabClick = remember<() -> Unit> {
                        {
                            scope.launch {
                                snackbarHost.showSnackbar(
                                    message = "Hello, Responsive Scaffold! " +
                                            "It's nice to have you by my side! <3"
                                )
                            }
                        }
                    }

                    val navItems by lazy {
                        listOf(
                            NavItem(
                                selectedIcon = Icons.Filled.Home,
                                unselectedIcon = Icons.Outlined.Home,
                                label = "Home"
                            ),
                            NavItem(
                                selectedIcon = Icons.Filled.Favorite,
                                unselectedIcon = Icons.Outlined.FavoriteBorder,
                                label = "Fav"
                            ),
                            NavItem(
                                selectedIcon = Icons.Filled.AccountCircle,
                                unselectedIcon = Icons.Outlined.AccountCircle,
                                label = "Profile"
                            ),
                        )
                    }

                    ResponsiveScaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            LargeTopAppBar(
                                title = { Text(text = stringResource(id = R.string.app_name)) },
                                scrollBehavior = scrollBehavior,
                                windowInsets = ResponsiveScaffoldUtils.topAppBarWindowInsets()
                            )
                        },
                        bottomBar = {
                            NavigationBottomBar(
                                navItems = navItems,
                                selectedItemIndex = selectedItemIndex,
                                onSelectedItemIndex = remember { { selectedItemIndex = it } }
                            )
                        },
                        sideRail = {
                            NavigationSideRail(
                                navItems = navItems,
                                selectedItemIndex = selectedItemIndex,
                                onSelectedItemIndex = remember { { selectedItemIndex = it } },
                                header = {
                                    FAB(
                                        onClick = onFabClick,
                                        elevation = FloatingActionButtonDefaults
                                            .bottomAppBarFabElevation()
                                    )
                                }
                            )
                        },
                        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
                        floatingActionButton = {
                            FAB(
                                onClick = onFabClick,
                                elevation = FloatingActionButtonDefaults.elevation()
                            )
                        }
                    ) { paddingValues ->

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            state = lazyListState,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = paddingValues
                        ) {

                            item { Spacer(modifier = Modifier.height(16.dp)) }

                            items(count = 100, key = { "item_$it" }) { i ->
                                Text(text = "Item: ${i + 1}")
                            }

                            item { Spacer(modifier = Modifier.height(16.dp)) }

                        }

                    }

                }
            }
        }
    }

}

@Immutable
data class NavItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

@Composable
fun FAB(
    onClick: () -> Unit,
    elevation: FloatingActionButtonElevation
) {

    FloatingActionButton(
        onClick = onClick,
        elevation = elevation
    ) {

        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add"
        )

    }

}

@Composable
fun NavigationSideRail(
    navItems: List<NavItem>,
    selectedItemIndex: Int,
    onSelectedItemIndex: (Int) -> Unit,
    header: @Composable () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxHeight(),
        verticalAlignment = Alignment.Bottom
    ) {

        NavigationRail(
            header = {

                Spacer(modifier = Modifier.height(6.dp))

                header()

            }
        ) {

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(
                    12.dp, Alignment.Bottom
                )
            ) {

                navItems.forEachIndexed { i, item ->

                    val selected = i == selectedItemIndex

                    NavigationRailItem(
                        selected = selected,
                        onClick = remember { { onSelectedItemIndex(i) } },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon
                                else item.unselectedIcon,
                                contentDescription = null
                            )
                        },
                        label = { Text(text = item.label) }
                    )

                }

                Spacer(modifier = Modifier.height(12.dp))

            }

        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
        )

    }

}

@Composable
fun NavigationBottomBar(
    navItems: List<NavItem>,
    selectedItemIndex: Int,
    onSelectedItemIndex: (Int) -> Unit,
) {

    NavigationBar {

        navItems.forEachIndexed { i, item ->

            val selected = i == selectedItemIndex

            NavigationBarItem(
                selected = selected,
                onClick = remember { { onSelectedItemIndex(i) } },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon
                        else item.unselectedIcon,
                        contentDescription = null
                    )
                },
                label = { Text(text = item.label) }
            )

        }

    }

}