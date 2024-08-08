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
) : ViewModel() {

    private val _uiState = MutableLiveData<FinishedListUiState>()
    val uiState: LiveData<FinishedListUiState> = _uiState

    private var _finishedItemListUiEvent = SingleLiveEvent<FinishedListViewModelEvent>()
    val finishedItemListUiEvent: LiveData<FinishedListViewModelEvent> = _finishedItemListUiEvent

    private var _deleteFinishedItemListUiEvent = SingleLiveEvent<DeleteFinishedListViewModelEvent>()
    val deleteFinishedItemListUiEvent:
            LiveData<DeleteFinishedListViewModelEvent> = _deleteFinishedItemListUiEvent

    fun getAllFinishedItemList(){
        _uiState.value = FinishedListUiState.Loading
        viewModelScope.launch {
            itemRepository.getAllFinishedItems().fold(
                onSuccess = {
                    _finishedItemListUiEvent.value = FinishedListViewModelEvent.Success(it)
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
        _uiState.value = FinishedListUiState.Loading
        viewModelScope.launch {
            itemRepository.deleteAllFinishedItems().fold(
                onSuccess = {
                    _deleteFinishedItemListUiEvent.value = DeleteFinishedListViewModelEvent.Success
                    getAllFinishedItemList()
                },
                onFailure = {
                    _deleteFinishedItemListUiEvent.value = DeleteFinishedListViewModelEvent.Error(
                        it.message ?: "Something went wrong"
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
sealed class DeleteFinishedListViewModelEvent {
    data object Success: DeleteFinishedListViewModelEvent()
    data class Error(val error: String) : DeleteFinishedListViewModelEvent()
}
