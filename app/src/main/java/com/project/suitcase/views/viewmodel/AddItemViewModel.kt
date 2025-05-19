package com.project.suitcase.views.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.domain.repository.ItemRepository
import com.project.suitcase.domain.repository.TripRepository
import com.project.suitcase.domain.model.TripDetailModel
import com.project.suitcase.views.viewmodel.util.SingleLiveEvent
import kotlinx.coroutines.launch

class AddItemViewModel(
    private val itemRepository: ItemRepository,
    private val tripRepository: TripRepository
): ViewModel() {

    private val _uiState = MutableLiveData<AddItemUiState>()
    val uiState: LiveData<AddItemUiState> = _uiState

    private val _addItemUiEvent = SingleLiveEvent<AddItemViewModelEvent>()
    val addItemUiEvent: LiveData<AddItemViewModelEvent> = _addItemUiEvent

    private var _tripListUiEvent = SingleLiveEvent<AddItemGetTripsViewModelEvent>()
    val tripListUiEvent: LiveData<AddItemGetTripsViewModelEvent> = _tripListUiEvent

    fun addItem(
        tripId: String,
        tripName: String,
        itemName: String,
        itemDescription: String,
        itemLocation: String,
        itemImage: Uri?,
        itemPrice: String,
    ) {
        _uiState.value = AddItemUiState.Loading
        viewModelScope.launch {
            itemRepository.addItem(
                tripId = tripId,
                tripName = tripName,
                itemPrice = itemPrice,
                itemDescription = itemDescription,
                itemLocation = itemLocation,
                itemImage = itemImage,
                itemName = itemName,
                finished = false,
            ).fold(
                onSuccess = {itemId ->
                    _addItemUiEvent.value = AddItemViewModelEvent.Success(itemId)
                    Log.d("ItemListViewModel", "addItem: itemId=$itemId, tripId=$tripId, itemName=$itemName")
                },
                onFailure = {
                    _addItemUiEvent.value = AddItemViewModelEvent.Error(it.message?:
                    "Something went wrong")
                }
            )
        }

    }

    fun getTrips(){

        viewModelScope.launch {
            tripRepository.getTrips().fold(
                onFailure = {
                    _tripListUiEvent.value = AddItemGetTripsViewModelEvent.Error(it.message?:
                    "Something went rerong")
                },
                onSuccess = {
                    _tripListUiEvent.value = AddItemGetTripsViewModelEvent.Success(it)
                }
            )
        }
    }



}
sealed class AddItemUiState {
    data object Loading : AddItemUiState()
}
sealed class AddItemViewModelEvent {
    data class Success(val itemId: String): AddItemViewModelEvent()
    data class Error(val error: String): AddItemViewModelEvent()
}
sealed class AddItemGetTripsViewModelEvent {
    data class Success(val trips: List<TripDetailModel>) : AddItemGetTripsViewModelEvent()
    data class Error(val error: String) : AddItemGetTripsViewModelEvent()
}

