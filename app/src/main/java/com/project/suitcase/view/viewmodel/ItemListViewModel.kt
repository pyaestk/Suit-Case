package com.project.suitcase.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.data.repository.ItemRepository
import com.project.suitcase.data.repository.TripRepository
import com.project.suitcase.domain.model.ItemDetailModel
import kotlinx.coroutines.launch

class ItemListViewModel(
    private val tripRepository: TripRepository,
    private val itemRepository: ItemRepository
): ViewModel() {

    private val _uiState = MutableLiveData<ItemListUiState>()
    val uiState: LiveData<ItemListUiState> = _uiState

    private var _itemListUiEvent = MutableLiveData<ItemListViewModelEvent>()
    val itemListUiEvent: LiveData<ItemListViewModelEvent> = _itemListUiEvent

    fun getItemsByTrip(tripId: String) {
        _uiState.value = ItemListUiState.Loading
        viewModelScope.launch {
            itemRepository.getItemsByTrip(
                tripId = tripId
            ).fold(
                onSuccess = {
                    _uiState.value = ItemListUiState.Success(it)
                },
                onFailure = {
                    _itemListUiEvent.value = ItemListViewModelEvent.Error(it.message?:
                    "Something went wrong")
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
            ).fold(
                onSuccess = {
                    _uiState.value = ItemListUiState.UpdateSUccess
//                    getItemsByTrip(tripId)
                },
                onFailure = {
                    _itemListUiEvent.value = ItemListViewModelEvent.Error(
                        it.message?:"Something went wrong"
                    )
                }
            )
        }
    }

    fun markAllItemsAsFinished(tripId: String){
        _uiState.value = ItemListUiState.Loading
        viewModelScope.launch {
            itemRepository.markAllItemsAsFinished(tripId).fold(
                onSuccess = {
                    _uiState.value = ItemListUiState.MarkAllFinishedSuccess
                },
                onFailure = {
                    _itemListUiEvent.value = ItemListViewModelEvent.Error(
                        it.message?:"Something went wrong"
                    )
                }
            )
        }
    }

    fun deleteAllItems(tripId: String) {
        _uiState.value = ItemListUiState.Loading
        viewModelScope.launch {
            itemRepository.deleteAllItems(tripId).fold(
                onFailure = {
                    _itemListUiEvent.value = ItemListViewModelEvent.Error(
                        it.message?:"Something went wrong"
                    )
                },
                onSuccess = {
                    _uiState.value = ItemListUiState.DeleteAllSuccess
                    getItemsByTrip(tripId)
                }
            )
        }
    }

    fun deleteItem(tripId: String, itemId: String) {
        _uiState.value = ItemListUiState.Loading
        viewModelScope.launch {
            itemRepository.deleteItem(tripId = tripId, itemId = itemId).fold(
                onSuccess = {
                    _uiState.value = ItemListUiState.DeleteSuccess
                    getItemsByTrip(tripId)
                },
                onFailure = {
                    _itemListUiEvent.value = ItemListViewModelEvent.Error(
                        it.message?:"Something went wrong"
                    )
                }
            )
        }
    }



}

sealed class ItemListUiState {
    data object Loading : ItemListUiState()
    data class Success(val itemList: List<ItemDetailModel>): ItemListUiState()
    data object DeleteAllSuccess:ItemListUiState()
    data object DeleteSuccess:ItemListUiState()
    data object UpdateSUccess:ItemListUiState()
    data object MarkAllFinishedSuccess: ItemListUiState()
}
sealed class ItemListViewModelEvent {
    data class Error(val error: String) : ItemListViewModelEvent()
}