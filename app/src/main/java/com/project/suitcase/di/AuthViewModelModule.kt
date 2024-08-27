package com.project.suitcase.di

import com.project.suitcase.view.viewmodel.LoginViewModel
import com.project.suitcase.view.viewmodel.RegisterViewModel
import com.project.suitcase.view.viewmodel.util.ValidatorImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val AuthViewModelModule = module {
    viewModel{
        RegisterViewModel(
            authRepository = get(),
            validator = ValidatorImpl(
                get()
            )
        )
    }
    viewModel {
        LoginViewModel(
            authRepository = get(),
            validator = ValidatorImpl(
                get()
            )
        )
    }
}