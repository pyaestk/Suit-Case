package com.project.suitcase.di

import com.project.suitcase.view.viewmodel.ItemViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val itemViewModelModule = module {
    viewModel {
        ItemViewModel(
            get()
        )
    }
}