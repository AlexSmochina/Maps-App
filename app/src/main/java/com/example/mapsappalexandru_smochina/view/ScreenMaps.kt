package com.example.mapsappalexandru_smochina.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun Screen_Maps(navigationController: NavHostController) {
    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold (topBar = { MyTopAppBar() },) { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
            ){
                MapScreen()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(){
    TopAppBar(
        title = {
            Text(
                text = "Maps",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Red,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = { /* Handle navigation icon click */ }) {
                Icon(imageVector = Icons.Filled.List, contentDescription = "List")
            }
        },
        actions = {
            IconButton(onClick = { /* Handle action icon click */ }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun MapScreen(){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        val itb = LatLng(41.4534265,2.1837151)
        val cameraPositionState = rememberCameraPositionState{
            position = CameraPosition.fromLatLngZoom(itb,10f)
        }
        GoogleMap (
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState) {
            Marker(
                state = MarkerState(position = itb),
                title = "ITB",
                snippet = "Marker at ITB"
            )
        }
    }
}