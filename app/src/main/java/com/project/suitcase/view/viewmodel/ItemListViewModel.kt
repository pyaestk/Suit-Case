package com.project.suitcase.view.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.data.repository.ItemRepository
import com.project.suitcase.data.repository.TripRepository
import com.project.suitcase.domain.model.ItemDetailModel
import com.project.suitcase.view.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ItemListViewModel(
    private val tripRepository: TripRepository,
    private val itemRepository: ItemRepository
): ViewModel() {

    private val _uiState = MutableLiveData<ItemListUiState>()
    val uiState: LiveData<ItemListUiState> = _uiState

    private var _itemListUiEvent = SingleLiveEvent<ItemListViewModelEvent>()
    val itemListUiEvent: LiveData<ItemListViewModelEvent> = _itemListUiEvent

    private var _itemListDeleteUiEvent = SingleLiveEvent<ItemListDeleteViewModelEvent>()
    val itemListDeleteUiEvent: LiveData<ItemListDeleteViewModelEvent> = _itemListDeleteUiEvent

    fun getItemsByTrip(tripId: String) {
        _uiState.value = ItemListUiState.Loading
        viewModelScope.launch {
            itemRepository.getItemsByTrip(
                tripId = tripId
            ).fold(
                onSuccess = {
                    _itemListUiEvent.value = ItemListViewModelEvent.Success(it)
                },
                onFailure = {
                    _itemListUiEvent.value = ItemListViewModelEvent.Error(it.message?:
                    "Something went wrong")
                }
            )
        }
    }

    fun updateItemStatus(itemId: String, finished: Boolean, tripId: String) {
        viewModelScope.launch {
            itemRepository.updateCheckedItemStatus(
                itemId = itemId,
                finished = finished,
                tripId = tripId
            )
            Log.d("ItemListViewModel", "updateItemStatus: itemId=$itemId, finished=$finished, tripId=$tripId")
        }
    }

    fun deleteAllItems(tripId: String) {
        _uiState.value = ItemListUiState.Loading
        viewModelScope.launch {
            itemRepository.deleteAllItems(tripId).fold(
                onFailure = {
                    _itemListDeleteUiEvent.value = ItemListDeleteViewModelEvent.Error(
                        it.message ?: "Something went wrong"
                    )
                },
                onSuccess = {
                    _itemListDeleteUiEvent.value = ItemListDeleteViewModelEvent.Success
                    getItemsByTrip(tripId)
                }
            )
        }
    }



    fun moveToFinished(tripId: String, itemId: String) {
        viewModelScope.launch {
            itemRepository.moveToFinished(
                tripId = tripId,
                itemId = itemId
            )
        }
    }

    fun removeFromFinished(itemId: String){
        viewModelScope.launch {
            itemRepository.removeFromFinished(itemId)
        }
    }

    fun  getFinishedItemList(){
        viewModelScope.launch {
            itemRepository.getFinishedItemList()
        }
    }

}

sealed class ItemListUiState {
    data object Loading : ItemListUiState()
}
sealed class ItemListViewModelEvent {
    data class Success(val itemList: List<ItemDetailModel>) : ItemListViewModelEvent()
    data class Error(val error: String) : ItemListViewModelEvent()
}

sealed class ItemListDeleteViewModelEvent{
    data object Success: ItemListDeleteViewModelEvent()
    data class Error(val error: String): ItemListDeleteViewModelEvent()
}
