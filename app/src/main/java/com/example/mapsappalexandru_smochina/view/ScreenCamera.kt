package com.example.mapsappalexandru_smochina.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.navigation.NavHostController
import com.example.mapsappalexandru_smochina.Routes
import com.example.mapsappalexandru_smochina.viewModel.myViewModel

@Composable
fun Screen_Camera(navigationController: NavHostController, viewModel: myViewModel) {
    val context = LocalContext.current
    val isCameraPermissionGranted by viewModel.cameraPermissionGranted.observeAsState(false)
    val shouldShowPermissionRationale by viewModel.shouldShowPermissionRationale.observeAsState(false)
    val showPermissionDenied by viewModel.showPermissionDenied.observeAsState(false)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted){
                viewModel.setCameraPermissionGranted(true)
            }else {
                viewModel.setShouldShowPermissionRationale(
                    shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.CAMERA
                    )
                )
                if (!shouldShowPermissionRationale) {
                    Log.i("CameraScreen", "No podemos a pedir permisos")
                    viewModel.setShowPermissionDenied(true)
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            if (!isCameraPermissionGranted){
                launcher.launch(Manifest.permission.CAMERA)
            }else{
                navigationController.navigate(Routes.TakePhotoScreen.route)
            }
        }) {
            Text(text = "Take photo")
        }
    }
    if (showPermissionDenied){
        PermissionDeclinedScreen()
    }
}

@Composable
fun PermissionDeclinedScreen(){
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Permission required", fontWeight = FontWeight.Bold)
        Text(text = "This app needs access to the camera to take photos")
        Button(onClick = {
            openAppSettings(context as Activity)
        }) {
            Text(text = "Accept")
        }
    }
}

fun openAppSettings(activity: Activity) {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package",activity.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    activity.startActivity(intent)
}