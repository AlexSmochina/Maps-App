package com.example.mapsappalexandru_smochina.view

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.mapsappalexandru_smochina.R
import com.example.mapsappalexandru_smochina.Routes
import com.example.mapsappalexandru_smochina.model.UserPrefs
import com.example.mapsappalexandru_smochina.viewModel.myViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Screen_Login(navigationController: NavHostController, viewModel: myViewModel) {

    val emailState: String by viewModel.emailState.observeAsState("")
    val passwordState: String by viewModel.passwordState.observeAsState("")

    val context = LocalContext.current
    val userPrefs = UserPrefs(context)
    val storedUserData = userPrefs.getUserData.collectAsState(initial = emptyList())
    val validLogin: Boolean by viewModel.validLogin.observeAsState(true)
    val goToNext: Boolean by viewModel.goToNext.observeAsState(false)
    val permanecerLogged: Boolean by viewModel.permanecerLogged.observeAsState(false)
    val token = "556322939575-5utrpu3cio0v06ihvkf4odkqpdf9plvl.apps.googleusercontent.com"

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                viewModel.signInWithGoogleCredential(credential) {
                    viewModel.modifyProcessing(true)
                    navigationController.navigate(Routes.ScreenMaps.route)
                }
                if (account.email != null) viewModel.modifyLoggedUser(account.email!!)
                if (permanecerLogged) {
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.saveUserData(viewModel.getLoggedUser(), "")
                    }
                }
            } catch (e: Exception) {
                Log.d("MascotaFeliz", "GoogleSign fallo")
            }
        }

    val opciones = GoogleSignInOptions
        .Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
        .requestIdToken(token)
        .requestEmail()
        .build()
    val googleSignInCliente = GoogleSignIn.getClient(context, opciones)

    if (storedUserData.value.isNotEmpty() && storedUserData.value[0] != ""
        && storedUserData.value[1] != "" && validLogin
    ) {
        viewModel.modifyProcessing(false)
        viewModel.login(storedUserData.value[0], storedUserData.value[1])
        if (goToNext) {
            navigationController.navigate(Routes.ScreenMaps.route)
        }
    } else if (storedUserData.value.isNotEmpty() && storedUserData.value[0] != "") {
        viewModel.modifyProcessing(false)
        launcher.launch(googleSignInCliente.signInIntent)

    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable._10818), contentDescription = "Login")
        Text(text = "LOGIN", fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp), color = Color.Black)

        TextField(
            value = emailState,
            onValueChange ={ viewModel.modificarEmailState(it)},
            label = { Text(text = "Mail")}
        )
        Spacer(modifier = Modifier.padding(5.dp))

        TextField(value =passwordState,
            onValueChange = {viewModel.modificarPasswordState(it)},
            label = { Text(text = "Enter password")},
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.padding(5.dp))

        Row(Modifier.wrapContentSize()) {
            Text(
                text = "Permanecer conectado :",
                Modifier.align(Alignment.CenterVertically),
                color = Color.Black // Color del texto
            )
            Checkbox(
                checked = permanecerLogged,
                onCheckedChange = { isChecked ->
                    viewModel.cambiarPermanecerLogged(isChecked)
                })
            Spacer(modifier = Modifier.width(8.dp))
        }

        Button(
            onClick = {
                if (passwordState.length < 6) {
                viewModel.modificarShowDialogPass(true)
                viewModel.modificarPasswordProblem(true)
            } else if (emailState.contains("@")) {
                viewModel.login(emailState, passwordState)
                if (permanecerLogged) {
                    CoroutineScope(Dispatchers.IO).launch {
                        userPrefs.saveUserData(emailState, passwordState)
                    }
                }
            } else {
                viewModel.modificarPasswordProblem(false)
                viewModel.modificarShowDialogPass(true)
            }}
        ) {
            Text(text = "Login", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Row {
            Text(text = "Don't have an account? ")
            Column {
                Text(text ="Sign Up", modifier = Modifier.clickable { navigationController.navigate(Routes.ScreenSignUp.route) }, color = Color(4, 158, 241))
            }
        }
        Row(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    launcher.launch(googleSignInCliente.signInIntent)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable._000px_google_g_logo_svg_),
                contentDescription = "Login de google",
                modifier = Modifier
                    .padding(10.dp)
                    .size(40.dp)
            )
            Text(text = "Login con Google", fontSize = 18.sp, modifier = Modifier.padding(end = 10.dp))
        }
    }
}

@Composable
fun MyDialogPasswordOrEmail(show: Boolean, password: Boolean, onDismiss: () -> Unit) {
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                if (password) {
                    Text(text = "La contraseña debe ser mínimo de 6 caracteres")
                } else {
                    Text(text = "El email es irróneo, necesitas mínimo el @")
                }
            }
        }
    }
}

@Composable
fun MyDialogPasswordAuth(show: Boolean, emailProblem: Boolean, onDismiss: () -> Unit) {
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                if (emailProblem) {
                    Text(
                        text = "Email ya registrado!!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Credenciales incorrectas",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}