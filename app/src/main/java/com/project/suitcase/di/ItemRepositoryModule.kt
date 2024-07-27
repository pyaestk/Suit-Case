package com.project.suitcase.di

import com.project.suitcase.data.repository.ItemRepository
import org.koin.dsl.module

val itemRepositoryModule = module {
    single {
        ItemRepository(
            get()
        )
    }
}