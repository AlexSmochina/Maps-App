package com.example.mapsappalexandru_smochina

sealed class Routes(val route: String) {
    object SplashScreen : Routes("SplashScreen")
    object ScreenMaps : Routes("ScreenMaps")
    object ScreenCamera : Routes("ScreenCamera")

}