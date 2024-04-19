package com.example.mapsappalexandru_smochina.viewModel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsappalexandru_smochina.model.Marker
import com.example.mapsappalexandru_smochina.model.Repository
import com.example.mapsappalexandru_smochina.model.Usuario
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class myViewModel : ViewModel() {
    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    private val _loadingMaps = MutableLiveData(true)
    val loadingMaps = _loadingMaps

    private val _showBottomSheet = MutableLiveData(false)
    val showBottomSheet: LiveData<Boolean> = _showBottomSheet



    private val _takenPhoto = MutableLiveData<Bitmap?>(null)
    val takenPhoto: MutableLiveData<Bitmap?> = _takenPhoto

    private val _photoUri = MutableLiveData<Uri?>(null)
    val photoUri: MutableLiveData<Uri?> = _photoUri

    fun storePhoto(photo: Bitmap, uri: Uri) {
        _takenPhoto.value = photo
        _photoUri.value = uri
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
                    "imagen" to marker.picture,
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
                "imagen" to editedMarker.picture,
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

    fun uploadImage(imageUri: Uri) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storage = FirebaseStorage.getInstance().getReference("images/$fileName")
        storage.putFile(imageUri)
            .addOnSuccessListener {
                Log.i("IMAGE UPLOAD", "Image upload successfully")
                storage.downloadUrl.addOnSuccessListener {
                    Log.i("IMAGEN", it.toString())
                }
            }
            .addOnFailureListener{
                Log.i("IMAGE UPLOAD","Image upload successfully")
            }
    }

}