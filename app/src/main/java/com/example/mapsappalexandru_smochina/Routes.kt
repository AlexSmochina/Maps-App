package com.example.mapsappalexandru_smochina

sealed class Routes(val route: String) {
    object ScreenLogin : Routes("ScreenLogin")
    object ScreenMaps : Routes("ScreenMaps")
    object ScreenCamera : Routes("ScreenCamera")
    object TakePhotoScreen : Routes("TakePhotoScreen")
    object ScreenListMaps : Routes("ScreenListMaps")
    object ScreenGallery : Routes("ScreenGallery")
    object ScreenSignUp : Routes("ScreenSignUp")
    object ScreenMenu : Routes("ScreenMenu")
}