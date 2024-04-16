package com.example.mapsappalexandru_smochina.model

data class Marker(
    var markerId:String?,
    var longitud: Double,
    var latitud: Double,
    var title: String,
    var snippet: String
){
    constructor() : this(null,0.0,0.0,"","")
}