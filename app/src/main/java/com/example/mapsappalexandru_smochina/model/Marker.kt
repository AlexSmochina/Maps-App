package com.example.mapsappalexandru_smochina.model

import com.google.android.gms.maps.model.LatLng

data class Marker(
    val latLng: LatLng,
    val title: String,
    val snippet: String
){

}