package com.example.mapsappalexandru_smochina.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.example.mapsappalexandru_smochina.R
import com.example.mapsappalexandru_smochina.Routes
import com.example.mapsappalexandru_smochina.viewModel.myViewModel
import java.io.File

@Composable
fun TakePhoto_Screen(navigationController: NavHostController, viewModel: myViewModel) {
    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            CameraController.IMAGE_CAPTURE
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
        IconButton(
            onClick = {
                controller.cameraSelector = if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }else{
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
            }, modifier = Modifier.offset(16.dp,16.dp)
        ) {
            Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = "Switch camera")
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                IconButton(onClick = { navigationController.navigate(Routes.ScreenGallery.route) }) {
                    Icon(imageVector = Icons.Default.Photo, contentDescription = "Open gallery")
                }
                IconButton(onClick = {
                    takePhoto(context,controller,viewModel) { photo, uri ->
                        navigationController.navigateUp()
                    }
                }) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take photo")
                }
            }
        }
    }
}

private fun takePhoto(context: Context, controller: LifecycleCameraController, viewModel: myViewModel, onPhotoTake: (Bitmap, Uri) -> Unit) {
    val outputDirectory = getOutputDirectory(context)
    val photoFile = File(outputDirectory, "${System.currentTimeMillis()}.jpg")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    controller.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),

        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                viewModel.storePhoto(bitmap, savedUri)
                onPhotoTake(bitmap, savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("Camera", "Error take photo", exception)
            }
        }
    )
}

@Composable
fun CameraPreview(controller: LifecycleCameraController, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        }, modifier = modifier
    )
}

private fun getOutputDirectory(context: Context): File {
    val appContext = context.applicationContext
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else appContext.filesDir
}