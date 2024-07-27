package com.project.suitcase.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.suitcase.data.datasource.TripRemoteDataSource
import org.koin.dsl.module

val tripDataSourceModule = module {
    single {
        TripRemoteDataSource(
            firebaseAuth = FirebaseAuth.getInstance(),
            fireStore = FirebaseFirestore.getInstance()
        )
    }
}