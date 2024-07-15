package com.project.suitcase

import android.app.Application
import com.project.suitcase.di.auth.AuthDatasourceModule
import com.project.suitcase.di.auth.AuthRepositoryModule
import com.project.suitcase.di.auth.AuthViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SuitCaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                AuthDatasourceModule,
                AuthRepositoryModule,
                AuthViewModelModule
            )
            androidContext(this@SuitCaseApplication)
        }

    }
}