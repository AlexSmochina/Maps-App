package com.example.mapsappalexandru_smochina.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mapsappalexandru_smochina.R
import com.example.mapsappalexandru_smochina.Routes
import com.example.mapsappalexandru_smochina.viewModel.myViewModel

@Composable
fun Screen_Login(navigationController: NavHostController, viewModel: myViewModel) {

    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable._10818), contentDescription = "Login")
        Text(text = "LOGIN", fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp), color = Color.Black)

        TextField(
            value = user,
            onValueChange ={ user = it},
            label = { Text(text = "Username")}
        )
        Spacer(modifier = Modifier.padding(5.dp))

        TextField(value =password,
            onValueChange = {password = it},
            label = { Text(text = "Enter password")},
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.padding(5.dp))

        Button(
            onClick = {navigationController.navigate(Routes.ScreenMaps.route)}
        ) {
            Text(text = "Login", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Row {
            Text(text = "Don't have an account? ")
            Column {
                Text(text ="Sign Up", modifier = Modifier.clickable { navigationController.navigate(Routes.ScreenSignUp.route) }, color = Color(4, 158, 241))
            }
        }
    }

}