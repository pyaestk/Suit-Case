package com.project.suitcase.di

import com.project.suitcase.data.repository.AuthRepository
import org.koin.dsl.module

val AuthRepositoryModule = module {
    single {
        AuthRepository(
            get()
        )
    }
}