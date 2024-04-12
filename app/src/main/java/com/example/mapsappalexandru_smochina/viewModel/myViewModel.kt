package com.example.mapsappalexandru_smochina.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsappalexandru_smochina.model.Marker
import com.example.mapsappalexandru_smochina.model.Usuario
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class myViewModel : ViewModel() {
    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    // Lista mutable para almacenar los marcadores agregados
    private val markersList = mutableListOf<Marker>()

    // Función para agregar un marcador con título y snippet
    fun addMarker(latLng: LatLng, title: String, snippet: String) {
        // Crea un nuevo marcador con los datos proporcionados
        val newMarker = Marker(latLng, title, snippet)

        // Agrega el nuevo marcador a la lista de marcadores
        markersList.add(newMarker)

    }

    fun getMarkersList(): List<Marker> {
        return markersList.toList()
    }

    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied: Boolean) {
        _showPermissionDenied.value = denied
    }

    //--------------------username----------------------

    private val database = FirebaseFirestore.getInstance()

    fun addUser(user: Usuario) {
        database.collection("users")
            .add(
                hashMapOf(
                    "name" to user.name,
                    "userName" to user.userName,
                    "mail" to user.mail,
                    "password" to user.password
                )
            )
    }

    fun editUser(editedUser:Usuario) {
        database.collection("users").document(editedUser.userId!!).set(
            hashMapOf(
                "name" to editedUser.name,
                "userName" to editedUser.userName,
                "mail" to editedUser.mail,
                "password" to editedUser.password
            )
        )
    }

    fun deleteUser(userId: String) {
        database.collection("users").document(userId).delete()
    }

}