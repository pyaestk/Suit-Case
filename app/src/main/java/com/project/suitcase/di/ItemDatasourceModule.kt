package com.project.suitcase.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.suitcase.data.datasource.ItemRemoteDatasource
import org.koin.dsl.module

val itemDatasourceModule = module {
    single {
        ItemRemoteDatasource(
            firebaseAuth = FirebaseAuth.getInstance(),
            fireStore = FirebaseFirestore.getInstance(),
            fStorage = FirebaseStorage.getInstance()
        )
    }
}