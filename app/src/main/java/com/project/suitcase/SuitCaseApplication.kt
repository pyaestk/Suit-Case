package com.project.suitcase

import android.app.Application
import com.project.suitcase.di.AuthDatasourceModule
import com.project.suitcase.di.AuthRepositoryModule
import com.project.suitcase.di.AuthViewModelModule
import com.project.suitcase.di.itemDatasourceModule
import com.project.suitcase.di.itemRepositoryModule
import com.project.suitcase.di.tripDataSourceModule
import com.project.suitcase.di.tripRepositoryModule
import com.project.suitcase.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SuitCaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                AuthDatasourceModule,
                AuthRepositoryModule,
                AuthViewModelModule,

                tripDataSourceModule,
                tripRepositoryModule,

                itemDatasourceModule,
                itemRepositoryModule,

                viewModelModule
            )
            androidContext(this@SuitCaseApplication)
        }

    }
}