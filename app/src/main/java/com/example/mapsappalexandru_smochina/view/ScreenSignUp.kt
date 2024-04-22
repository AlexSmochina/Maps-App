package com.example.mapsappalexandru_smochina.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mapsappalexandru_smochina.R
import com.example.mapsappalexandru_smochina.Routes
import com.example.mapsappalexandru_smochina.model.UserPrefs
import com.example.mapsappalexandru_smochina.model.Usuario
import com.example.mapsappalexandru_smochina.viewModel.myViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Screen_Sign_Up(navigationController: NavHostController, viewModel: myViewModel) {

    val emailState: String by viewModel.emailState.observeAsState("")
    val passwordState: String by viewModel.passwordState.observeAsState("")
    val nombreState: String by viewModel.nombreState.observeAsState("")
    val userNameState: String by viewModel.userNameState.observeAsState("")
    var verifyPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val userPrefs = UserPrefs(context)
    val storedUserData = userPrefs.getUserData.collectAsState(initial = emptyList())
    val validLogin: Boolean by viewModel.validLogin.observeAsState(true)
    val goToNext: Boolean by viewModel.goToNext.observeAsState(false)
    val permanecerLogged: Boolean by viewModel.permanecerLogged.observeAsState(false)
    val showDialogPass: Boolean by viewModel.showDialogPass.observeAsState(false)
    val passwordProblem: Boolean by viewModel.passwordProblem.observeAsState(false)
    val showDialogAuth: Boolean by viewModel.showDialogAuth.observeAsState(false)
    val emailProblem: Boolean by viewModel.emailDuplicated.observeAsState(false)

    if (storedUserData.value.isNotEmpty() && storedUserData.value[0] != ""
        && storedUserData.value[1] != "" && validLogin
    ) {
        viewModel.modifyProcessing(false)
        viewModel.login(storedUserData.value[0], storedUserData.value[1])
        if (goToNext) {
            navigationController.navigate(Routes.ScreenMaps.route)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable._10818), contentDescription = "register")
        Text(
            text = "REGISTER",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(5.dp),
            color = Color.Black
        )

        TextField(
            value = nombreState,
            onValueChange = { viewModel.modificarNombreState(it) },
            label = {
                Text(text = "Full name")
            }
        )

        Spacer(modifier = Modifier.padding(5.dp))

        TextField(
            value = userNameState,
            onValueChange = { viewModel.modificarUserNameState(it) },
            label = { Text(text = "Username") }
        )

        Spacer(modifier = Modifier.padding(5.dp))

        TextField(value = emailState,
            onValueChange = { viewModel.modificarEmailState(it) },
            label = {
                Text(text = "Email address")
            }
        )
        Spacer(modifier = Modifier.padding(5.dp))

        TextField(
            value = passwordState,
            onValueChange = { viewModel.modificarPasswordState(it) },
            label = { Text(text = "Enter password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.padding(5.dp))

        TextField(
            value = verifyPassword,
            onValueChange = { verifyPassword = it },
            label = { Text(text = "Verify password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
            Spacer(modifier = Modifier.padding(5.dp))
        }

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
                if (passwordState.length < 6 || passwordState == verifyPassword) {
                    viewModel.modificarShowDialogPass(true)
                    viewModel.modificarPasswordProblem(true)
                    errorMessage = "Las contraseñas no coinciden"
                } else if (emailState.contains("@")) {
                    if (permanecerLogged) {
                        CoroutineScope(Dispatchers.IO).launch {
                            userPrefs.saveUserData(emailState, passwordState)
                        }
                    }
                    viewModel.register(context, emailState, passwordState)
                    viewModel.addUser(Usuario(null,nombreState,userNameState,emailState,passwordState))
                    navigationController.navigate(Routes.ScreenLogin.route)
                } else {
                    viewModel.modificarPasswordProblem(false)
                    viewModel.modificarShowDialogPass(true)
                }
            }
        ) {
            Text(
                text = "Register",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        MyDialogPasswordOrEmail(
            showDialogPass,
            passwordProblem
        ) { viewModel.modificarShowDialogPass(false) }

        MyDialogPasswordAuth(
            showDialogAuth,
            emailProblem
        ) { viewModel.modificarShowDialogAuth(false) }
    }
}