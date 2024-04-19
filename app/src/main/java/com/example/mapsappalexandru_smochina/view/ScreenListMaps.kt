package com.example.mapsappalexandru_smochina.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mapsappalexandru_smochina.model.Marker
import com.example.mapsappalexandru_smochina.viewModel.myViewModel

@Composable
fun Screen_List_Maps(navigationController: NavHostController, viewModel: myViewModel) {
    val markers by viewModel.markerList.observeAsState(emptyList())
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    Column(
        modifier = Modifier
            .background(Color(20, 169, 242))
            .fillMaxSize()
    ) {
        MyTopAppBar(state = state)
        Spacer(modifier = Modifier.height(8.dp))
        // Muestra la lista de marcadores en un LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(markers) { marker ->
                MarkerItem(marker = marker)
            }
        }
    }
}

@Composable
fun MarkerItem(marker: Marker) {
    Card(
        border = BorderStroke(2.dp, Color.LightGray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        // Aquí defines cómo se muestra cada marcador en la lista
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = marker.title, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = marker.snippet, color = Color.Black)
            // Puedes agregar más información si es necesario
        }
    }
}