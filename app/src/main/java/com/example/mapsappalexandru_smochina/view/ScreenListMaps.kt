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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mapsappalexandru_smochina.model.Marker
import com.example.mapsappalexandru_smochina.viewModel.myViewModel

@Composable
fun Screen_List_Maps(navigationController: NavHostController, viewModel: myViewModel) {
    val markers by viewModel.markerList.observeAsState(emptyList())

    var selectedIcon by remember { mutableStateOf("mundo") }

    LaunchedEffect(selectedIcon) {
        viewModel.getMarkerCategory(selectedIcon)
    }

    if (!viewModel.userLogged()) {
        viewModel.signOut(context = LocalContext.current, navigationController)
    }

    MyDrawer(viewModel = viewModel, navigationController = navigationController) {
        Column(
            modifier = Modifier
                .background(Color(22, 164, 235))
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Icon: ", fontWeight = FontWeight.Bold, color = Color.Black)
                viewModel.icons.forEach { icon ->
                    val vectorName = icon
                    val context = LocalContext.current
                    val vectorId = context.resources.getIdentifier(vectorName, "drawable", context.packageName)
                    val vectorResource = painterResource(id = vectorId)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = vectorResource,
                        contentDescription = "Icon",
                        modifier = Modifier.size(25.dp)
                    )
                    RadioButton(
                        selected = (icon == selectedIcon),
                        onClick = { selectedIcon = icon },
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(markers) { marker ->
                    if (marker.category == selectedIcon) {
                        MarkerItem(marker = marker, viewModel)
                    }
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
                Text(text = "Titulo: ${marker.title}", fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "Snippet: ${marker.snippet}", color = Color.Black)
                Text(text = "Category: ${marker.category}", color = Color.Black)
            }
            IconButton(onClick = { marker.markerId?.let { viewModel.deleteMarker(it) } }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Marker")
            }
        }
    }
}