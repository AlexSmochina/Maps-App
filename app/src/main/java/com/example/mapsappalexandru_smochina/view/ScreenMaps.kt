package com.example.mapsappalexandru_smochina.view

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mapsappalexandru_smochina.Routes
import com.example.mapsappalexandru_smochina.viewModel.myViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Screen_Maps(navigationController: NavHostController, myViewModel: myViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val permissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
        if (permissionState.status.isGranted){
            MyDrawer(myViewModel)
        }else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(text = "Need permission")
            }
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyDrawer(
    myViewModel: myViewModel,
) {
    val navigationController = rememberNavController()
    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(drawerState = state, gesturesEnabled = false, drawerContent = {
        ModalDrawerSheet {
            Text(text = "Drawer title", modifier = Modifier.padding(16.dp))
            Divider()
            NavigationDrawerItem(
                label = { Text(text = "Mapa") },
                selected = false,
                onClick = {
                    if (navigationController.currentDestination?.route != Routes.ScreenMaps.route) {
                        navigationController.navigate(Routes.ScreenMaps.route)
                    }
                }
            )
            NavigationDrawerItem(
                label = { Text(text = "Lista marcadores") },
                selected = false,
                onClick = {

                }
            )
            Column (
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    onClick = {
                    scope.launch {
                        state.close()
                    }
                }) {
                    Text(text = "Volver")
                }
            }
        }
    }) {
        MyScaffold(myViewModel,state)
    }
}

@Composable
fun MyScaffold(
    myViewModel: myViewModel,
    state: DrawerState
) {
    Column {
        MyTopAppBar(state = state)
        BottomSheet(
            viewModel = myViewModel,
            onAddMarker = { latLng, title, snippet ->
                myViewModel.addMarker(latLng, title, snippet)
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(state: DrawerState) {
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
fun MapScreen(
    myViewModel: myViewModel,
    onMapLongClick: (LatLng) -> Unit // Función de devolución de llamada para el evento de mantener presionado
) {
    val markers = myViewModel.getMarkersList()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(41.4534265, 2.1837151), 10f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLongClick = { latLng ->
            onMapLongClick(latLng)
        }
    ) {
        markers.forEachIndexed { index, marker ->
            Marker(
                state = MarkerState(position = marker.latLng),
                title = marker.title,
                snippet = marker.snippet
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    viewModel: myViewModel,
    onAddMarker: (LatLng, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var snippet by remember { mutableStateOf("") }
    var markerLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    Scaffold(
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier
                        .padding(26.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Icon(Icons.Filled.Add, "Floating action button.")
                }
            }
        }
    ) { contentPadding ->

        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            MapScreen(viewModel) { latLng ->
                markerLatLng = latLng
                showBottomSheet = true
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = snippet,
                        onValueChange = { snippet = it },
                        label = { Text("Snippet") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "camera",
                        modifier = Modifier
                            .size(100.dp)
                            .align(alignment = Alignment.CenterHorizontally)
                    )

                    Button(
                        onClick = {
                            // Agrega el marcador con la posición y la información proporcionada
                            onAddMarker(markerLatLng, title, snippet)
                            // Oculta el modal bottom sheet
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Add Marker")
                    }
                }
            }
        }
    }
}
