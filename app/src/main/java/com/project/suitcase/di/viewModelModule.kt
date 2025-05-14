package com.project.suitcase.di

import com.project.suitcase.ui.viewmodel.AddItemViewModel
import com.project.suitcase.ui.viewmodel.AddTripViewModel
import com.project.suitcase.ui.viewmodel.FinishedListViewModel
import com.project.suitcase.ui.viewmodel.HomeFragmentViewModel
import com.project.suitcase.ui.viewmodel.ItemDetailViewModel
import com.project.suitcase.ui.viewmodel.ItemListViewModel
import com.project.suitcase.ui.viewmodel.SearchViewModel
import com.project.suitcase.ui.viewmodel.UserProfileViewModel
import com.project.suitcase.ui.viewmodel.WelcomeScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        ItemListViewModel(
            itemRepository = get(),
            tripRepository = get()
        )
    }
    viewModel{
        AddItemViewModel(
            itemRepository = get(),
            tripRepository = get()
        )
    }

    viewModel {
        HomeFragmentViewModel(
            tripRepository = get(),
            itemRepository = get()
        )
    }
    viewModel {
        AddTripViewModel(
            get()
        )
    }
    viewModel {
        FinishedListViewModel(
            get()
        )
    }
    viewModel {
        SearchViewModel(
            get()
        )
    }
    viewModel {
        ItemDetailViewModel(
            itemRepository = get(),
            tripRepository = get()
        )
    }
    viewModel {
        UserProfileViewModel(
            get()
        )
    }
    viewModel {
        WelcomeScreenViewModel(
            get()
        )
    }
}