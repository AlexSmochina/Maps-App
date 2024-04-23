package com.example.mapsappalexandru_smochina.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mapsappalexandru_smochina.Routes
import com.example.mapsappalexandru_smochina.model.Marker
import com.example.mapsappalexandru_smochina.model.Repository
import com.example.mapsappalexandru_smochina.model.UserPrefs
import com.example.mapsappalexandru_smochina.model.Usuario
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class myViewModel : ViewModel() {

    val icons = listOf(
        "baseline_park_24",
        "baseline_school_24",
        "baseline_restaurant_24",
        "baseline_shopping_cart_24"
    )

    // -------------------- TAKE FOTO AND TAKE URL --------------------
    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied: Boolean) {
        _showPermissionDenied.value = denied
    }
    // ----------------------------------------------------------------

    // -------------------- LOADING MAP --------------------
    private val _loadingMaps = MutableLiveData(true)
    val loadingMaps = _loadingMaps
    // -----------------------------------------------------

    // -------------------- SHOW BOTTOM SHEET --------------------
    private val _showBottomSheet = MutableLiveData(false)
    val showBottomSheet: LiveData<Boolean> = _showBottomSheet
    // -----------------------------------------------------------

    // -------------------- TAKE FOTO AND TAKE URL --------------------
    private val _takenPhoto = MutableLiveData<Bitmap?>(null)
    val takenPhoto: MutableLiveData<Bitmap?> = _takenPhoto

    private val _photoUri = MutableLiveData<Uri?>(null)
    val photoUri: MutableLiveData<Uri?> = _photoUri

    fun storePhoto(photo: Bitmap, uri: Uri) {
        _takenPhoto.value = photo
        _photoUri.value = uri
    }
    // ----------------------------------------------------------------

    // -------------------- TAKE POSITION --------------------
    private var position = LatLng(41.4534265, 2.1837151)
    fun changePosition(positionNueva: LatLng) {
        position = positionNueva
    }

    fun getPosition(): LatLng {
        return position
    }
    // -------------------------------------------------------

    // -------------------- MARKER --------------------
    private val database = FirebaseFirestore.getInstance()

    private var _markerList = MutableLiveData<MutableList<Marker>>()
    var markerList: LiveData<MutableList<Marker>> = _markerList

    fun addMarker(marker: Marker) {
        database.collection("marker")
            .add(
                hashMapOf(
                    "owner" to _loggedUser.value,
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
        repository.getMarkers()
            .whereEqualTo("owner", _loggedUser.value)
            .addSnapshotListener{ value, error ->
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
    // ------------------------------------------------

    // -------------------- USER ----------------------
    private var _goToNext = MutableLiveData(true)
    val goToNext: LiveData<Boolean> = _goToNext

    private val _userId = MutableLiveData<String>()
    private val _loggedUser = MutableLiveData<String>()
    val loggedUser = _loggedUser

    fun getLoggedUser(): String {
        return _loggedUser.value.toString()
    }

    fun modifyLoggedUser(nuevo: String) {
        _loggedUser.value = nuevo
    }

    private val _showDialogAuth = MutableLiveData<Boolean>()
    val showDialogAuth: LiveData<Boolean> = _showDialogAuth

    fun modificarShowDialogAuth(value: Boolean) {
        _showDialogAuth.value = value
    }

    private val _processing = MutableLiveData<Boolean>()
    val processing: LiveData<Boolean> = _processing

    // LiveData para emailState
    private val _emailState = MutableLiveData<String>()
    val emailState: LiveData<String> = _emailState

    //Email duplicado
    private val _emailDuplicated = MutableLiveData<Boolean>()
    val emailDuplicated: LiveData<Boolean> = _emailDuplicated

    // LiveData para passwordState
    private val _passwordState = MutableLiveData<String>()
    val passwordState: LiveData<String> = _passwordState

    // LiveData para nombreState
    private val _nombreState = MutableLiveData<String>()
    val nombreState: LiveData<String> = _nombreState

    private val _userNameState = MutableLiveData<String>()
    val userNameState: LiveData<String> = _userNameState

    private val _permanecerLogged = MutableLiveData<Boolean>()
    val permanecerLogged = _permanecerLogged

    // LiveData para showDialog
    private val _showDialogPass = MutableLiveData<Boolean>()
    val showDialogPass: LiveData<Boolean> = _showDialogPass

    // LiveData para passwordProblem
    private val _passwordProblem = MutableLiveData<Boolean>()
    val passwordProblem: LiveData<Boolean> = _passwordProblem

    fun cambiarPermanecerLogged(nuevoBoolean: Boolean) {
        _permanecerLogged.value = nuevoBoolean
    }
    fun modificarShowDialogPass(value: Boolean) {
        _showDialogPass.value = value
    }

    fun modificarPasswordProblem(value: Boolean) {
        _passwordProblem.value = value
    }
    fun modifyProcessing(newValue: Boolean) {
        _processing.value = newValue
    }

    fun modificarEmailState(value: String) {
        _emailState.value = value
    }

    fun modificarPasswordState(value: String) {
        _passwordState.value = value
    }

    fun modificarNombreState(value: String) {
        _nombreState.value = value
    }

    fun modificarUserNameState(value: String) {
        _userNameState.value = value
    }

    private val _validLogin = MutableLiveData<Boolean>()
    val validLogin: LiveData<Boolean> = _validLogin

    private val auth = FirebaseAuth.getInstance()

    fun deleteUser(userId: String) {
        database.collection("users").document(userId).delete()
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

    fun register(context: Context, username: String, password: String) {
        val userPrefs = UserPrefs(context)
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email
                    _goToNext.value = true
                    modifyProcessing(false)
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.saveUserData(_emailState.value!!, _passwordState.value!!)
                    }
                    val userRef =
                        database.collection("user").whereEqualTo("owner", _loggedUser.value)
                    userRef.get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                // Si no hay documentos para este usuario, agregar uno nuevo
                                database.collection("user")
                                    .add(
                                        hashMapOf(
                                            "owner" to _loggedUser.value,
                                            "name" to _nombreState.value,
                                            "userName" to _userNameState.value,
                                        )
                                    )
                            }
                        }
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error creating user : ${task.exception}")
                    modifyProcessing(true)
                    _emailDuplicated.value = true
                    _showDialogAuth.value = true
                }
            }
    }

    fun login(username: String?, password: String?) {
        auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email
                    _goToNext.value = true
                    modifyProcessing(false)
                    // Agregar el marcador a la base de datos con la referencia de la foto actualizada
                    // Verificar si el usuario ya tiene un documento en la colección "user"
                    val userRef =
                        database.collection("user").whereEqualTo("owner", _loggedUser.value)
                    userRef.get()
                        .addOnSuccessListener { documents ->
                            if (documents.isEmpty) {
                                // Si no hay documentos para este usuario, agregar uno nuevo
                                database.collection("user")
                                    .add(
                                        hashMapOf(
                                            "owner" to _loggedUser.value,
                                            "name" to _nombreState.value,
                                            "userName" to _userNameState.value,
                                            // "password" to usuari.password (es logico guardar la contraseña rarete, no?)
                                        )
                                    )
                            }
                        }
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error signing in: ${task.exception}")
                    modifyProcessing(true)
                    _emailDuplicated.value = false
                    _showDialogAuth.value = true
                }
            }
            .addOnFailureListener {
                _validLogin.value = false
            }
    }

    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            modifyProcessing(false)
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MascotaFeliz", "Log con exito")
                            val userRef =
                                database.collection("user").whereEqualTo("owner", _loggedUser.value)
                            userRef.get()
                                .addOnSuccessListener { documents ->
                                    if (documents.isEmpty) {
                                        // Si no hay documentos para este usuario, agregar uno nuevo
                                        database.collection("user")
                                            .add(
                                                hashMapOf(
                                                    "owner" to _loggedUser.value,
                                                )
                                            )
                                    }
                                }
                            home()
                        }
                    }
                    .addOnFailureListener {
                        Log.d("MascotaFeliz", "Fallo al loguear")
                    }
            } catch (ex: Exception) {
                Log.d("MascotaFeliz", "Excepción al hacer log" + ex.localizedMessage)
            }
        }

    fun signOut(context: Context, navController: NavController) {

        val userPrefs = UserPrefs(context)

        CoroutineScope(Dispatchers.IO).launch {
            userPrefs.deleteUserData()
        }

        auth.signOut()

        _goToNext.value = false
        _passwordState.value = ""

        modifyProcessing(true)
        navController.navigate(Routes.ScreenLogin.route)
    }

    fun userLogged(): Boolean {
        return auth.currentUser != null
    }
    // ------------------------------------------------
    val screen = mutableStateOf("Unknown")

    fun setScreen(screenName: String) {
        screen.value = screenName
    }

}