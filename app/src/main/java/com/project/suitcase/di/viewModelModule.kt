package com.project.suitcase.di

import com.project.suitcase.view.viewmodel.AddItemViewModel
import com.project.suitcase.view.viewmodel.AddTripViewModel
import com.project.suitcase.view.viewmodel.FinishedListViewModel
import com.project.suitcase.view.viewmodel.HomeFragmentViewModel
import com.project.suitcase.view.viewmodel.ItemDetailViewModel
import com.project.suitcase.view.viewmodel.ItemListViewModel
import com.project.suitcase.view.viewmodel.SearchViewModel
import com.project.suitcase.view.viewmodel.UserProfileViewModel
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
}