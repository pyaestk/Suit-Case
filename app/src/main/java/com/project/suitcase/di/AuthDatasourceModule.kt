package com.project.suitcase.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.suitcase.data.datasource.AuthRemoteDatasource
import org.koin.dsl.module

val AuthDatasourceModule = module {
    single {
        AuthRemoteDatasource(
            firebaseAuth = FirebaseAuth.getInstance(),
            firestore = FirebaseFirestore.getInstance(),
            fStorage = FirebaseStorage.getInstance()
        )
    }
}