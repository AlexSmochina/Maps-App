package com.example.mapsappalexandru_smochina.model

data class Marker(
    val longitud: Double,
    val latitud: Double,
    var title: String,
    val snippet: String
){
    constructor() : this(0.0,0.0,"","")
}