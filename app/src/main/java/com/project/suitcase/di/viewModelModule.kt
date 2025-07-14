package com.project.suitcase.di

import com.project.suitcase.views.viewmodel.AddItemViewModel
import com.project.suitcase.views.viewmodel.AddTripViewModel
import com.project.suitcase.views.viewmodel.FinishedListViewModel
import com.project.suitcase.views.viewmodel.HomeFragmentViewModel
import com.project.suitcase.views.viewmodel.ItemDetailViewModel
import com.project.suitcase.views.viewmodel.ItemListViewModel
import com.project.suitcase.views.viewmodel.SearchViewModel
import com.project.suitcase.views.viewmodel.UserProfileViewModel
import com.project.suitcase.views.viewmodel.WelcomeScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        ItemListViewModel(
            itemRepository = get()
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