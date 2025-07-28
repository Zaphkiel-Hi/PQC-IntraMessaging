package org.niklasunrau.pqcmessenger.presentation.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.niklasunrau.pqcmessenger.BuildConfig
import org.niklasunrau.pqcmessenger.R
import org.niklasunrau.pqcmessenger.presentation.util.Dimens
import org.niklasunrau.pqcmessenger.presentation.util.Either
import org.niklasunrau.pqcmessenger.presentation.util.NavigationItem
import org.niklasunrau.pqcmessenger.presentation.util.Screen


@Composable
fun CustomNavigationDrawer(
    drawerState: DrawerState,
    navigationItems: List<NavigationItem>,
    onNavigateToScreen: (Screen) -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val selectedItemIndex = remember {
        mutableStateOf(0)
    }
    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        BackHandler(enabled = drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        }
        ModalDrawerSheet(
            modifier = Modifier.requiredWidth(300.dp)
        ) {
            Row(
                modifier = Modifier.height(200.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_icon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight()
                )
                Spacer(modifier = Modifier.width(Dimens.SmallPadding))
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            HorizontalDivider()

            navigationItems.forEachIndexed { index, item ->
                NavigationDrawerItem(label = { Text(text = stringResource(id = item.titleId)) },
                    icon = { Icon(imageVector = item.icon, contentDescription = null) },
                    selected = selectedItemIndex.value == index,
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    onClick = {
                        if (selectedItemIndex.value != index) {
                            onNavigateToScreen(item.screen)
                            selectedItemIndex.value = index
                            scope.launch { drawerState.close() }
                        }
                    })
            }


            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.version) + BuildConfig.VERSION_NAME,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = Dimens.SmallPadding)
            )
        }
    }, content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomScaffold(
    title: Either<String, @Composable () -> Unit>,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ), title = {
                    when (title) {
                        is Either.Left -> Text(
                            text = title.a, color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        is Either.Right -> title.b()
                    }
                }, navigationIcon = navigationIcon, actions = actions
            )
        }, floatingActionButton = floatingActionButton, content = content
    )
}

@Composable
fun CustomDrawerScaffold(
    drawerState: DrawerState,
    title: Either<String, @Composable () -> Unit>,
    actions: @Composable (RowScope.() -> Unit) = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()

    CustomScaffold(
        title = title, navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.apply {
                        if (isClosed) open() else close()
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null
                )
            }
        }, actions = actions, floatingActionButton = floatingActionButton, content = content
    )
}