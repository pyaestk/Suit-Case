package com.project.suitcase.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.data.repository.ItemRepository
import com.project.suitcase.domain.model.ItemDetailModel
import com.project.suitcase.view.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class FinishedListViewModel(
    private var itemRepository: ItemRepository
):ViewModel() {

    private val _uiState = MutableLiveData<FinishedListUiState>()
    val uiState: LiveData<FinishedListUiState> = _uiState

    private var _itemListUiEvent = SingleLiveEvent<FinishedListViewModelEvent>()
    val itemListUiEvent: LiveData<FinishedListViewModelEvent> = _itemListUiEvent

    private var _removedFromFinishedUiEvent = SingleLiveEvent<RemoveFromFinishedViewModelEvent>()
    val removeFromFinishedViewModelEvent: LiveData<RemoveFromFinishedViewModelEvent> = _removedFromFinishedUiEvent

    fun getFinishedItems(){
        _uiState.value = FinishedListUiState.Loading
        viewModelScope.launch {
            itemRepository.getFinishedItemList().fold(
                onSuccess = {
                    _itemListUiEvent.value = FinishedListViewModelEvent.Success(it)
                },
                onFailure = {
                    _itemListUiEvent.value = FinishedListViewModelEvent.Error(it.message?:
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
            itemRepository.removeFromFinished(itemId).fold(
                onSuccess = {
                    _removedFromFinishedUiEvent.value = RemoveFromFinishedViewModelEvent.Success
                },
                onFailure = {
                    _removedFromFinishedUiEvent.value = RemoveFromFinishedViewModelEvent.Error(
                        it.message?: "Something went wrong"
                    )
                }
            )
        }
    }
}
sealed class FinishedListUiState {
    data object Loading : FinishedListUiState()
}
sealed class FinishedListViewModelEvent {
    data class Success(val itemList: List<ItemDetailModel>) : FinishedListViewModelEvent()
    data class Error(val error: String) :FinishedListViewModelEvent()
}
sealed class RemoveFromFinishedViewModelEvent {
    data object Success : RemoveFromFinishedViewModelEvent()
    data class Error(val error: String) :RemoveFromFinishedViewModelEvent()
}