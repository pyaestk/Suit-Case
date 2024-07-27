package com.project.suitcase.di

import com.project.suitcase.view.viewmodel.LoginViewModel
import com.project.suitcase.view.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AuthViewModelModule = module {
    viewModel{
        RegisterViewModel(
            get()
        )
    }
    viewModel {
        LoginViewModel(
            get()
        )
    }
}