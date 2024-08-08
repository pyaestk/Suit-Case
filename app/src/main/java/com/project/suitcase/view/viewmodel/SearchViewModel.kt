package com.project.suitcase.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.data.repository.ItemRepository
import com.project.suitcase.domain.model.ItemDetailModel
import com.project.suitcase.view.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class SearchViewModel(
    private val itemRepository: ItemRepository
):ViewModel() {

    private var _itemListUiEvent = SingleLiveEvent<SearchListViewModelEvent>()
    val itemListUiEvent: LiveData<SearchListViewModelEvent> = _itemListUiEvent


    fun getAllItems(){
        viewModelScope.launch {
            itemRepository.getAllItems().fold(
                onFailure = {
                    _itemListUiEvent.value = SearchListViewModelEvent.Error(
                        it.message?: "Something went wrong"
                    )
                },
                onSuccess = {
                    _itemListUiEvent.value = SearchListViewModelEvent.Success(it)
                }
            )
        }
    }
}
sealed class SearchListUiState {
    data object Loading : SearchListUiState()
}
sealed class SearchListViewModelEvent {
    data class Success(val itemList: List<ItemDetailModel>) : SearchListViewModelEvent()
    data class Error(val error: String) : SearchListViewModelEvent()
}
