package com.project.suitcase.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.data.repository.TripRepository
import com.project.suitcase.domain.model.TripDetailModel
import kotlinx.coroutines.launch

class TripViewModel(
    private val tripRepository: TripRepository
): ViewModel() {

    private val _uiState = MutableLiveData<TripUiState>()
    val uiState: LiveData<TripUiState> = _uiState

    private val _addTripUiEvent = MutableLiveData<TripViewModelEvent>()
    val addTripUiEvent: LiveData<TripViewModelEvent> = _addTripUiEvent

    private val _tripListUiEvent = MutableLiveData<GetTripViewModelEvent>()
    val tripListUiEvent: LiveData<GetTripViewModelEvent> = _tripListUiEvent

    fun addTrip(
        tripName: String,
        date: String
    ) {
        _uiState.value = TripUiState.Loading
        viewModelScope.launch {
            tripRepository.addTrip(
                tripName, date
            ).fold(
               onSuccess = { tripId ->
                   getTripsAndItems()
                   _addTripUiEvent.value= TripViewModelEvent.Success(tripId)
               },
                onFailure = {
                    _addTripUiEvent.value = TripViewModelEvent.Error(it.message?:
                    "Something went wrong")
                }
            )
        }
    }

    fun getTripsAndItems() {
        _uiState.value = TripUiState.Loading
        viewModelScope.launch {
            tripRepository.getTripsAndItems().fold(
                onSuccess = {
                    _tripListUiEvent.value = GetTripViewModelEvent.Success(it)
                },
                onFailure = {
                    _tripListUiEvent.value = GetTripViewModelEvent.Error(it.message?:
                    "Something went wrong")
                }
            )
        }
    }

    fun getTrips(){
        _uiState.value = TripUiState.Loading
        viewModelScope.launch {
            tripRepository.getTrips().fold(
                onFailure = {
                    _tripListUiEvent.value = GetTripViewModelEvent.Error(it.message?:
                    "Something went rerong")
                },
                onSuccess = {
                    _tripListUiEvent.value = GetTripViewModelEvent.Success(it)
                }
            )
        }
    }


}

sealed class TripUiState {
    data object Loading : TripUiState()
}

sealed class GetTripViewModelEvent {
    data class Success(val trips: List<TripDetailModel>) : GetTripViewModelEvent()
    data class Error(val error: String) : GetTripViewModelEvent()
}

sealed class TripViewModelEvent {
    data class Success(val tripId: String): TripViewModelEvent()
    data class Error(val error: String) : TripViewModelEvent()
}