package com.example.mapsappalexandru_smochina

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsappalexandru_smochina.ui.theme.MapsAppAlexandru_SmochinaTheme
import com.example.mapsappalexandru_smochina.view.Screen_Camera
import com.example.mapsappalexandru_smochina.view.Screen_Gallery
import com.example.mapsappalexandru_smochina.view.Screen_List_Maps
import com.example.mapsappalexandru_smochina.view.Screen_Login
import com.example.mapsappalexandru_smochina.view.Screen_Maps
import com.example.mapsappalexandru_smochina.view.Screen_Sign_Up
import com.example.mapsappalexandru_smochina.view.TakePhoto_Screen
import com.example.mapsappalexandru_smochina.viewModel.myViewModel

class MainActivity : ComponentActivity() {
    val viewModel: myViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsAppAlexandru_SmochinaTheme {

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navigationController = rememberNavController()
                    NavHost(
                        navController = navigationController,
                        startDestination = Routes.ScreenLogin.route
                    ) {
                        composable(Routes.ScreenLogin.route) {
                            Screen_Login(
                                navigationController,
                                viewModel
                            )
                        }
                        composable(Routes.ScreenSignUp.route) {
                            Screen_Sign_Up(
                                navigationController,
                                viewModel
                            )
                        }
                        composable(Routes.ScreenMaps.route) {
                            Screen_Maps(
                                navigationController,
                                viewModel
                            )
                        }
                        composable(Routes.ScreenCamera.route) {
                            Screen_Camera(
                                navigationController,
                                viewModel
                            )
                        }
                        composable(Routes.TakePhotoScreen.route) {
                            TakePhoto_Screen(
                                navigationController,
                                viewModel
                            )
                        }
                        composable(Routes.ScreenListMaps.route) {
                            Screen_List_Maps(
                                navigationController,
                                viewModel,
                            )
                        }
                        composable(Routes.ScreenGallery.route) {
                            Screen_Gallery(
                                navigationController,
                                viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
