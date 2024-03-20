package com.example.mapsappalexandru_smochina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsappalexandru_smochina.ui.theme.MapsAppAlexandru_SmochinaTheme
import com.example.mapsappalexandru_smochina.view.Screen_Camera
import com.example.mapsappalexandru_smochina.view.Screen_Maps
import com.example.mapsappalexandru_smochina.view.Splash_Screen
import com.example.mapsappalexandru_smochina.viewModel.myViewModel

class MainActivity : ComponentActivity() {
    val viewModel: myViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsAppAlexandru_SmochinaTheme {

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navigationController = rememberNavController()
                    NavHost(
                        navController = navigationController,
                        startDestination = Routes.SplashScreen.route
                    ) {
                        composable(Routes.SplashScreen.route) {
                            Splash_Screen(
                                navigationController,
                            )
                        }
                        composable(Routes.ScreenMaps.route) {
                            Screen_Maps(
                                navigationController,
                                viewModel
                            )
                        }
                        composable(Routes.ScreenMaps.route) {
                            Screen_Camera(
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
