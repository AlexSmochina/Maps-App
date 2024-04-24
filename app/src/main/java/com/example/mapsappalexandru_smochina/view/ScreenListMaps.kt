package com.example.mapsappalexandru_smochina.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mapsappalexandru_smochina.model.Marker
import com.example.mapsappalexandru_smochina.viewModel.myViewModel

@Composable
fun Screen_List_Maps(navigationController: NavHostController, viewModel: myViewModel) {
    val markers by viewModel.markerList.observeAsState(emptyList())
    if (!viewModel.userLogged()) {
        viewModel.signOut(context = LocalContext.current, navigationController)
    }

    MyDrawer(viewModel = viewModel, navigationController = navigationController) {
        Column(
            modifier = Modifier
                .background(Color(20, 169, 242))
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(markers) { marker ->
                    MarkerItem(marker = marker,viewModel)
                }
            }
        }
    }
}

@Composable
fun MarkerItem(marker: Marker, viewModel: myViewModel) {
    Card(
        border = BorderStroke(2.dp, Color.LightGray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = marker.title, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = marker.snippet, color = Color.Black)
            }
            IconButton(onClick = { marker.markerId?.let { viewModel.deleteMarker(it) } }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Marker")
            }
        }
    }
}