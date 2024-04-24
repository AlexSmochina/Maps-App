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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Screen_Maps(navigationController: NavHostController, viewModel: myViewModel) {
    MyDrawer(
        viewModel = viewModel,
        navigationController = navigationController,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {  val context = LocalContext.current

                val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
                var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
                var deviceLatLng by remember { mutableStateOf(LatLng(0.0,0.0)) }
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

                val permissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
                LaunchedEffect(Unit) {
                    permissionState.launchPermissionRequest()
                }
                if (permissionState.status.isGranted){
                    MapScreen(viewModel,navigationController,cameraPositionState)
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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MapScreen(
    viewModel: myViewModel,
    navigationController: NavHostController,
    cameraPositionState: CameraPositionState
) {
    viewModel.getMarker()

    if (!viewModel.userLogged()){
        viewModel.signOut(context = LocalContext.current, navigationController)
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var snippet by remember { mutableStateOf("") }
    val marker by viewModel.markerList.observeAsState(emptyList())


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

                        var selectedIcon by remember { mutableStateOf("mundo") }

                        Row (
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Icon: ", fontWeight = FontWeight.Bold, color = Color.Black)
                            viewModel.icons.forEach { icon ->
                                val vectorName = icon
                                val context = LocalContext.current
                                val vectorId = context.resources.getIdentifier(vectorName, "drawable", context.packageName)
                                val vectorResource = painterResource(id = vectorId)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    painter = vectorResource,
                                    contentDescription = "Icon",
                                    modifier = Modifier.size(25.dp)
                                )
                                RadioButton(
                                    selected = (icon == selectedIcon),
                                    onClick = { selectedIcon = icon },
                                )
                            }
                        }

                        Button(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
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
                                viewModel.addMarker(com.example.mapsappalexandru_smochina.model.Marker(null,viewModel.getLoggedUser(),direcion.latitude, direcion.longitude,title, snippet, selectedIcon))
                                title = ""
                                snippet = ""
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

