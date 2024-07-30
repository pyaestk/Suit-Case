package com.project.suitcase.view.viewmodel

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

    private var _deleteAllItemListDeleteUiEvent = SingleLiveEvent<ItemListDeleteViewModelEvent>()
    val deleteAllItemListDeleteUiEvent: LiveData<ItemListDeleteViewModelEvent> = _deleteAllItemListDeleteUiEvent

    private var _updateItemCheckedStatusUiEvent = SingleLiveEvent<UpdateItemCheckedStatusViewModelEvent>()
    val updateItemCheckedStatusUiEvent: LiveData<UpdateItemCheckedStatusViewModelEvent> = _updateItemCheckedStatusUiEvent

    private var _deleteItemListDeleteUiEvent = SingleLiveEvent<ItemDeleteViewModelEvent>()
    val deleteItemListDeleteUiEvent: LiveData<ItemDeleteViewModelEvent> = _deleteItemListDeleteUiEvent


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

    fun updateItemCheckedStatus(itemId: String, finished: Boolean, tripId: String) {
        viewModelScope.launch {
            itemRepository.updateCheckedItemStatus(
                itemId = itemId,
                finished = finished,
                tripId = tripId
            ).fold(
                onSuccess = {
                    _updateItemCheckedStatusUiEvent.value = UpdateItemCheckedStatusViewModelEvent.Success
                },
                onFailure = {
                    _updateItemCheckedStatusUiEvent.value = UpdateItemCheckedStatusViewModelEvent.Error(
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
                    _deleteAllItemListDeleteUiEvent.value = ItemListDeleteViewModelEvent.Error(
                        it.message ?: "Something went wrong"
                    )
                },
                onSuccess = {
                    _deleteAllItemListDeleteUiEvent.value = ItemListDeleteViewModelEvent.Success
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
                    _deleteItemListDeleteUiEvent.value = ItemDeleteViewModelEvent.Success
                    getItemsByTrip(tripId)
                },
                onFailure = {
                    _deleteItemListDeleteUiEvent.value = ItemDeleteViewModelEvent.Error(
                        it.message ?:"Something went wrong"
                    )
                }
            )
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
sealed class ItemDeleteViewModelEvent{
    data object Success: ItemDeleteViewModelEvent()
    data class Error(val error: String): ItemDeleteViewModelEvent()
}

sealed class UpdateItemCheckedStatusViewModelEvent{
    data object Success: UpdateItemCheckedStatusViewModelEvent()
    data class Error(val error: String): UpdateItemCheckedStatusViewModelEvent()
}