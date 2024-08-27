package com.project.suitcase.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.data.repository.ItemRepository
import com.project.suitcase.domain.model.ItemDetailModel
import com.project.suitcase.view.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class SearchViewModel(
    private val itemRepository: ItemRepository
):ViewModel() {

    private var _uiState = MutableLiveData<SearchListUiState>()
    val uiState: LiveData<SearchListUiState> = _uiState

    private var _uiEvent = SingleLiveEvent<SearchListViewModelEvent>()
    val uiEvent: LiveData<SearchListViewModelEvent> = _uiEvent


    fun getAllItems(){
        viewModelScope.launch {
            itemRepository.getAllItems().fold(
                onFailure = {
                    _uiEvent.value = SearchListViewModelEvent.Error(
                        it.message?: "Something went wrong"
                    )
                },
                onSuccess = {
                    _uiState.value = SearchListUiState.Success(it)
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
                    _uiState.value = SearchListUiState.UpdateSuccess
                },
                onFailure = {
                    _uiEvent.value = SearchListViewModelEvent.Error(
                        it.message?: "Something went wrong"
                    )
                }
            )
        }
    }
}
sealed class SearchListUiState {
    data object Loading : SearchListUiState()
    data class Success(val itemList: List<ItemDetailModel>) : SearchListUiState()
    data object UpdateSuccess: SearchListUiState()
}
sealed class SearchListViewModelEvent {
    data class Error(val error: String) : SearchListViewModelEvent()
}
