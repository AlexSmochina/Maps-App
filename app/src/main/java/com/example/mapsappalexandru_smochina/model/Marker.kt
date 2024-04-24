package com.example.mapsappalexandru_smochina.model

data class Marker(
    var markerId:String?,
    var owner: String?,
    var longitud: Double,
    var latitud: Double,
    var title: String,
    var snippet: String,
    var category: String

){
    constructor() : this(null,null,0.0,0.0,"","", "")
}

