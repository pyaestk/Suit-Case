package com.project.suitcase.views.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.domain.repository.ItemRepository
import com.project.suitcase.domain.repository.TripRepository
import com.project.suitcase.domain.model.TripDetailModel
import kotlinx.coroutines.launch

class HomeFragmentViewModel(
    private val itemRepository: ItemRepository,
    private val tripRepository: TripRepository
): ViewModel() {

    private val _uiState = MutableLiveData<HomeFragmentUiState>()
    val uiState: LiveData<HomeFragmentUiState> = _uiState

    private val _uiEvent = MutableLiveData<HomeFragmentViewModelEvent>()
    val uiEvent: LiveData<HomeFragmentViewModelEvent> = _uiEvent

    fun getTripsAndItems() {
        viewModelScope.launch {
            tripRepository.getTripsIncludingItems().fold(
                onSuccess = {
                    _uiState.value = HomeFragmentUiState.TripListSuccess(it)
                },
                onFailure = {
                    _uiEvent.value = HomeFragmentViewModelEvent.Error(it.message?:
                    "Something went wrong")
                }
            )
        }
    }

    fun deleteAllTrip() {
        viewModelScope.launch {
            _uiState.value = HomeFragmentUiState.Loading
            tripRepository.deleteAllTrip().fold(
                onFailure = {
                    _uiEvent.value = HomeFragmentViewModelEvent.Error(
                        it.message ?: "Something went wrong"
                    )
                },
                onSuccess = {
                    _uiState.value = HomeFragmentUiState.AllTripDeleteSuccess
                    getTripsAndItems()
                }
            )
        }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            tripRepository.deleteTrip(tripId).fold(
                onSuccess = {
                    _uiState.value = HomeFragmentUiState.TripListDeleteSuccess
                },
                onFailure = {
                    _uiEvent.value = HomeFragmentViewModelEvent.Error(
                        it.message ?: "Something went wrong"
                    )
                }
            )
        }
    }

    fun editTrip(
        tripId: String,
        tripName: String? = null,
        date: String? = null
    ) {
        viewModelScope.launch {
            tripRepository.editTrip(tripId, tripName, date).fold(
                onSuccess = {
                    _uiState.value = HomeFragmentUiState.TripEditSuccess
                },
                onFailure = {
                    _uiEvent.value = HomeFragmentViewModelEvent.Error(
                        it.message ?: "Something went wrong"
                    )
                }
            )
        }
    }

    fun updateItemCheckedStatus(itemId: String, finished: Boolean, tripId: String) {
        viewModelScope.launch {
            itemRepository.updateCheckedItemStatus(
                itemId = itemId,
                finished = finished,
                tripId = tripId
            )
        }
    }

}

sealed class HomeFragmentUiState {
    data object Loading : HomeFragmentUiState()
    data class TripListSuccess(val trips: List<TripDetailModel>) : HomeFragmentUiState()
    data object AllTripDeleteSuccess: HomeFragmentUiState()
    data object TripListDeleteSuccess: HomeFragmentUiState()
    data object TripEditSuccess: HomeFragmentUiState()
}

sealed class HomeFragmentViewModelEvent {
    data class Error(val error: String) : HomeFragmentViewModelEvent()
}
