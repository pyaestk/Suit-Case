package com.project.suitcase.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.data.repository.ItemRepository
import com.project.suitcase.data.repository.TripRepository
import com.project.suitcase.domain.model.TripDetailModel
import kotlinx.coroutines.launch

class HomeFragmentViewModel(
    private val itemRepository: ItemRepository,
    private val tripRepository: TripRepository
): ViewModel() {

    private val _uiState = MutableLiveData<HomeFragmentUiState>()
    val uiState: LiveData<HomeFragmentUiState> = _uiState

    private val _tripListUiEvent = MutableLiveData<HomeFragmentViewModelEvent>()
    val tripListUiEvent: LiveData<HomeFragmentViewModelEvent> = _tripListUiEvent

    fun getTripsAndItems() {
        _uiState.value = HomeFragmentUiState.Loading
        viewModelScope.launch {
            tripRepository.getTripsAndItems().fold(
                onSuccess = {
                    _tripListUiEvent.value = HomeFragmentViewModelEvent.Success(it)
                },
                onFailure = {
                    _tripListUiEvent.value = HomeFragmentViewModelEvent.Error(it.message?:
                    "Something went wrong")
                }
            )
        }
    }

}

sealed class HomeFragmentUiState {
    data object Loading : HomeFragmentUiState()
}

sealed class HomeFragmentViewModelEvent {
    data class Success(val trips: List<TripDetailModel>) : HomeFragmentViewModelEvent()
    data class Error(val error: String) : HomeFragmentViewModelEvent()
}