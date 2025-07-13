package com.project.suitcase.views.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.domain.repository.ItemRepository
import com.project.suitcase.domain.model.ItemDetailModel
import com.project.suitcase.views.viewmodel.util.SingleLiveEvent
import kotlinx.coroutines.launch

class FinishedListViewModel(
    private var itemRepository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<FinishedListUiState>()
    val uiState: LiveData<FinishedListUiState> = _uiState

    private var _finishedItemListUiEvent = SingleLiveEvent<FinishedListViewModelEvent>()
    val finishedItemListUiEvent: LiveData<FinishedListViewModelEvent> = _finishedItemListUiEvent

    fun getAllFinishedItemList(){
        viewModelScope.launch {
            itemRepository.getAllFinishedItems().fold(
                onSuccess = {
                    _uiState.value = FinishedListUiState.Success(it)
                },
                onFailure = {
                    _finishedItemListUiEvent.value = FinishedListViewModelEvent.Error(
                        it.message?:"Something went wrong"
                    )
                }

            )
        }
    }

    fun deleteAllFinishedItemList(){
        viewModelScope.launch {
            _uiState.value = FinishedListUiState.Loading
            itemRepository.deleteAllFinishedItems().fold(
                onSuccess = {
                    _uiState.value = FinishedListUiState.DeleteAllSuccess
                    getAllFinishedItemList()
                },
                onFailure = {
                    _finishedItemListUiEvent.value = FinishedListViewModelEvent.Error(
                        it.message?:"Something went wrong"
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
            ).fold(
                onSuccess = {
                    _uiState.value = FinishedListUiState.UpdateSuccess
                    getAllFinishedItemList()
                },
                onFailure = {
                    _finishedItemListUiEvent.value = FinishedListViewModelEvent.Error(
                        it.message?:"Something went wrong"
                    )
                }
            )
        }
    }

    fun deleteItem(tripId: String, itemId: String) {
        _uiState.value = FinishedListUiState.Loading
        viewModelScope.launch {
            itemRepository.deleteItem(tripId = tripId, itemId = itemId)
        }
    }


}
sealed class FinishedListUiState {
    data object Loading : FinishedListUiState()
    data class Success(val itemList: List<ItemDetailModel>): FinishedListUiState()
    data object DeleteAllSuccess: FinishedListUiState()
    data object UpdateSuccess: FinishedListUiState()
}
sealed class FinishedListViewModelEvent {
    data class Error(val error: String) :FinishedListViewModelEvent()
}
