package com.project.suitcase.views.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.domain.repository.TripRepository
import com.project.suitcase.views.viewmodel.util.SingleLiveEvent
import kotlinx.coroutines.launch

class AddTripViewModel(
    private val tripRepository: TripRepository
): ViewModel() {
    private val _uiState = MutableLiveData<AddTripUiState>()
    val uiState: LiveData<AddTripUiState> = _uiState

    private val _addTripUiEvent = SingleLiveEvent<AddTripViewModelEvent>()
    val addTripUiEvent: LiveData<AddTripViewModelEvent> = _addTripUiEvent

    fun addTrip(
        tripName: String,
        date: String
    ) {
        _uiState.value = AddTripUiState.Loading
        viewModelScope.launch {
            tripRepository.addTrip(
                tripName, date
            ).fold(
                onSuccess = { tripId ->
                    _addTripUiEvent.value= AddTripViewModelEvent.Success(tripId)
                },
                onFailure = {
                    _addTripUiEvent.value = AddTripViewModelEvent.Error(it.message?:
                    "Something went wrong")
                }
            )
        }
    }

}
sealed class AddTripUiState {
    data object Loading : AddTripUiState()
}
sealed class AddTripViewModelEvent {
    data class Success(val tripId: String): AddTripViewModelEvent()
    data class Error(val error: String) : AddTripViewModelEvent()
}
