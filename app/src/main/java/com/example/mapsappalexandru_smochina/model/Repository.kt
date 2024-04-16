package com.example.mapsappalexandru_smochina.model

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class Repository{

    private val database = FirebaseFirestore.getInstance()
    fun getMarkers(): CollectionReference {
        return database.collection("marker")
    }
}