package com.project.suitcase.di

import com.project.suitcase.data.repository.TripRepository
import org.koin.dsl.module

val tripRepositoryModule = module {
    single {
        TripRepository(
            tripRemoteDataSource = get()
        )
    }
}