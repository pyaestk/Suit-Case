package com.project.suitcase.di.auth

import com.project.suitcase.view.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AuthViewModelModule = module {
    viewModel{
        RegisterViewModel(
            get()
        )
    }
}