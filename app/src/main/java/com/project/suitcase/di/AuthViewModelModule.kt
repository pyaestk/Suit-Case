package com.project.suitcase.di

import com.project.suitcase.views.viewmodel.LoginViewModel
import com.project.suitcase.views.viewmodel.RegisterViewModel
import com.project.suitcase.views.viewmodel.util.ValidatorImpl
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