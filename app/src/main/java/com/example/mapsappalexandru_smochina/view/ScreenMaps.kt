package com.example.mapsappalexandru_smochina.view

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mapsappalexandru_smochina.MainActivity
import com.example.mapsappalexandru_smochina.Routes
import com.example.mapsappalexandru_smochina.viewModel.myViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Screen_Maps(navigationController: NavHostController, viewModel: myViewModel) {
    val showLoading: Boolean by viewModel.loadingMaps.observeAsState(true)
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val permissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
        if (permissionState.status.isGranted){
            if (showLoading){
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = Color(26, 11, 37, 255)
                    )
                }
            }else{
                MyDrawer(viewModel,navigationController)
            }
        }else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "No tienes permisos cabron mamon")
                    Button(
                        onClick = {

                        })
                    {
                        Text(text = "Para activar los permisos")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyDrawer(
    viewModel: myViewModel,
    navigationController: NavHostController
) {
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
                    scope.launch {
                        navigationController.navigate(Routes.ScreenMaps.route)
                    }
                }
            )
            NavigationDrawerItem(
                label = { Text(text = "Lista marcadores") },
                selected = false,
                onClick = {
                    navigationController.navigate(Routes.ScreenListMaps.route)
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
        MyScaffold(viewModel,state, navigationController)
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MyScaffold(
    viewModel: myViewModel,
    state: DrawerState,
    navigationController: NavHostController
) {
    Column {
        MyTopAppBar(state = state)
        MapScreen(viewModel = viewModel, navigationController = navigationController){

        }
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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MapScreen(
    viewModel: myViewModel,
    navigationController: NavHostController,
    onMapLongClick: (LatLng) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val showBottomSheet by viewModel.showBottomSheet.observeAsState(false)
    var title by remember { mutableStateOf("") }
    var snippet by remember { mutableStateOf("") }
    val context = LocalContext.current
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
    var deviceLatLng by remember { mutableStateOf(LatLng(0.0,0.0)) }
    val marker by viewModel.markerList.observeAsState(emptyList())
    viewModel.getMarker()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
    }
    val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
    locationResult.addOnCompleteListener(context as MainActivity) { task ->
        if (task.isSuccessful) {
            lastKnownLocation = task.result
            deviceLatLng =
                LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(deviceLatLng, 13f)
            viewModel.changePosition(deviceLatLng)
        } else {
            Log.e("Error", "Exception: %s", task.exception)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLongClick = {
            viewModel.changePosition(it)
            showBottomSheet = true
        },
        properties = MapProperties(
            isMyLocationEnabled = true,
            isTrafficEnabled = true
        )
    ) {
        marker.forEach { marker ->
            Marker(
                state = MarkerState(LatLng(marker.latitud, marker.longitud)),
                title = marker.title,
                snippet = marker.snippet
            )
        }
    }


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
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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

                        Button(
                            onClick = {
                                navigationController.navigate(Routes.ScreenCamera.route)
                                viewModel.photoUri.value?.let { uri ->
                                    viewModel.uploadImage(uri)
                                }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "camera",
                                modifier = Modifier

                            )
                        }

                        Button(
                            onClick = {
                                val direcion = viewModel.getPosition()
                                // Agrega el marcador con las coordenadas almacenadas en markerLatLng y la información proporcionada
                                viewModel.addMarker(com.example.mapsappalexandru_smochina.model.Marker(null,direcion.latitude, direcion.longitude, title, snippet))
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
}

