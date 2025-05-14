package com.project.suitcase.di

import com.project.suitcase.ui.viewmodel.LoginViewModel
import com.project.suitcase.ui.viewmodel.RegisterViewModel
import com.project.suitcase.ui.viewmodel.util.ValidatorImpl
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