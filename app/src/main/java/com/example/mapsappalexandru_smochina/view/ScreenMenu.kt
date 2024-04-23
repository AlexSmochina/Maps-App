package com.example.mapsappalexandru_smochina.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.mapsappalexandru_smochina.Routes
import com.example.mapsappalexandru_smochina.viewModel.myViewModel
import kotlinx.coroutines.launch

@Composable
fun MyScaffold(viewModel: myViewModel, state: DrawerState, navigationController: NavHostController, content: @Composable () -> Unit) {
    Scaffold (
        topBar = {
            MyTopAppBar(viewModel, state)
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                content()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(viewModel: myViewModel,state: DrawerState) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = { Text(text = "Maps APP") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Red,
            titleContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    state.open()
                }
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    )
}

@Composable
fun MyDrawer(viewModel: myViewModel, navigationController: NavHostController, content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    val currentRoute = navigationController.currentDestination?.route
    val drawerName = getScreenNameFromRoute(currentRoute ?: "")
    viewModel.setScreen(drawerName)
    val state = rememberDrawerState(initialValue = DrawerValue.Closed)

    if (!viewModel.userLogged()){
        viewModel.signOut(context = LocalContext.current, navigationController)
    }

    //ni idea como funciona pero cambia el titulo del appbar
    DisposableEffect(navigationController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val route = destination.route ?: return@OnDestinationChangedListener
            viewModel.setScreen(getScreenNameFromRoute(route))
        }
        navigationController.addOnDestinationChangedListener(listener)
        onDispose {
            navigationController.removeOnDestinationChangedListener(listener)
        }
    }
    ModalNavigationDrawer(
        drawerState = state,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet {
                Text(text = "Menú", modifier = Modifier.padding(20.dp), fontWeight = FontWeight.Bold )
                Divider()
                NavigationDrawerItem(
                    label = { Text("Maps") },
                    icon = { Icon(imageVector = Icons.Outlined.Place, contentDescription = "Maps") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            state.close()
                            navigationController.navigate(Routes.ScreenMaps.route)
                        }
                    }
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text("Lista Marcadores") },
                    icon = { Icon(imageVector = Icons.Outlined.List, contentDescription = "Lista") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            state.close()
                            navigationController.navigate(Routes.ScreenListMaps.route)
                        }
                    }
                )
                Divider()
                val context = LocalContext.current
                NavigationDrawerItem(
                    label = { Text("Cerrar Sesion") },
                    icon = { Icon(imageVector = Icons.Outlined.Logout, contentDescription = "Salir") },
                    selected = false,
                    onClick = {
                        viewModel.signOut(context,navigationController)
                    }
                )
            }
        }) {
        MyScaffold(viewModel, state, navigationController, content)
    }
}


fun getScreenNameFromRoute(route: String): String {
    return when (route) {
        Routes.ScreenMaps.route -> "Map"
        Routes.ScreenListMaps.route -> "List"
        else -> "Unknown"
    }
}