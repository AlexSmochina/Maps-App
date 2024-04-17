package com.example.mapsappalexandru_smochina.viewModel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsappalexandru_smochina.model.Marker
import com.example.mapsappalexandru_smochina.model.Repository
import com.example.mapsappalexandru_smochina.model.Usuario
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class myViewModel : ViewModel() {
    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    val takenPhoto = MutableLiveData<Bitmap?>(null)

    fun storePhoto(photo: Bitmap) {
        takenPhoto.value = photo
    }

    private var position = LatLng(41.4534265, 2.1837151)
    fun changePosition(positionNueva: LatLng) {
        position = positionNueva
    }

    fun getPosition(): LatLng {
        return position
    }

    private var _markerList = MutableLiveData<MutableList<Marker>>()
    var markerList: LiveData<MutableList<Marker>> = _markerList

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

    //--------------------marker----------------------

    fun addMarker(marker: Marker) {
        database.collection("marker")
            .add(
                hashMapOf(
                    "title" to marker.title,
                    "snippet" to marker.snippet,
                    "longitud" to marker.longitud,
                    "latitud" to marker.latitud
                )
            )

        getMarker()
    }

    fun editMarker(editedMarker: Marker) {
        database.collection("marker").document(editedMarker.title).set(
            hashMapOf(
                "title" to editedMarker.title,
                "snippet" to editedMarker.snippet,
                "longitud" to editedMarker.longitud,
                "latitud" to editedMarker.latitud
            )
        )
        getMarker()
    }

    fun deleteMarker(title: String) {
        database.collection("marker").document(title).delete()
    }

    var repository = Repository()
    fun getMarker() {
        repository.getMarkers().addSnapshotListener{ value, error ->
            if ( error!= null) {
                Log.e("Firestore error", error.message.toString())
                return@addSnapshotListener
            }
            val tempList = mutableListOf<Marker>()
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val newMarker = dc.document.toObject(Marker::class.java)
                    newMarker.markerId = dc.document.id
                    newMarker.latitud =
                        dc.document.get("longitud").toString().toDouble()
                    newMarker.longitud =
                        dc.document.get("latitud").toString().toDouble()
                    tempList.add(newMarker)
                }
            }
            _markerList.value = tempList
        }
    }

}