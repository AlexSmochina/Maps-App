package com.example.mapsappalexandru_smochina.model

class Usuario (
    var userId: String? = null,
    var name: String,
    var userName: String,
    var mail: String,
    var password: String,
){
    constructor(): this(null,"","","","")
}