package com.project.suitcase.di

import com.project.suitcase.view.viewmodel.TripViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tripViewModelModule = module {
    viewModel {
        TripViewModel(
            tripRepository = get()
        )
    }
}