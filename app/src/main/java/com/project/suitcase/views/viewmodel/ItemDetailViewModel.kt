package com.project.suitcase.views.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.domain.repository.ItemRepository
import com.project.suitcase.domain.repository.TripRepository
import com.project.suitcase.domain.model.ItemDetailModel
import com.project.suitcase.views.viewmodel.util.SingleLiveEvent
import kotlinx.coroutines.launch

class ItemDetailViewModel(

    private val itemRepository: ItemRepository
): ViewModel() {

    private val _uiState = MutableLiveData<ItemDetailUiState>()
    val uiState: LiveData<ItemDetailUiState> = _uiState

    private val _itemDetailUiEvent = SingleLiveEvent<ItemDetailViewModelEvent>()
    val itemDetailUiEvent: LiveData<ItemDetailViewModelEvent> = _itemDetailUiEvent

    fun getItemDetails(
        tripId: String,
        itemId: String
    ) {
        _uiState.value = ItemDetailUiState.Loading
        viewModelScope.launch {
            itemRepository.getItemDetails(
                tripId = tripId,
                itemId = itemId
            ).fold(
                onSuccess = {
                    _itemDetailUiEvent.value = ItemDetailViewModelEvent.Success(it)
                },
                onFailure = {
                    _itemDetailUiEvent.value = ItemDetailViewModelEvent.Error(
                        it.message ?: "Something went wrong"
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
            )
        }
    }

    fun editItemDetail(
        tripId: String,
        itemId: String,
        itemName: String?,
        itemDescription: String?,
        itemLocation: String?,
        itemImage: Uri?,
        itemPrice: String?,
    ) {
        _uiState.value = ItemDetailUiState.Loading
        viewModelScope.launch {
            itemRepository.editItem(
                tripId = tripId,
                itemId = itemId,
                itemPrice = itemPrice,
                itemDescription = itemDescription,
                itemLocation = itemLocation,
                itemImage = itemImage,
                itemName = itemName,
            ).fold(
                onSuccess = {
                    _itemDetailUiEvent.value = ItemDetailViewModelEvent.EditSuccess
                },
                onFailure = {
                    _itemDetailUiEvent.value = ItemDetailViewModelEvent.Error(
                        it.message ?: "Something went wrong"
                    )
                }

            )
        }
    }


    fun deleteItem(tripId: String, itemId: String) {
        viewModelScope.launch {
            itemRepository.deleteItem(tripId = tripId, itemId = itemId).fold(
                onSuccess = {
                    _itemDetailUiEvent.value = ItemDetailViewModelEvent.DeleteSuccess
                },
                onFailure = {
                    _itemDetailUiEvent.value = ItemDetailViewModelEvent.Error(
                        it.message ?: "Something went wrong"
                    )
                }
            )
        }
    }





}

sealed class ItemDetailUiState(){
    data object Loading: ItemDetailUiState()
}

sealed class ItemDetailViewModelEvent() {
    data class Success(val itemDetailModel: ItemDetailModel) : ItemDetailViewModelEvent()
    data class Error(val error: String) : ItemDetailViewModelEvent()
    data object EditSuccess: ItemDetailViewModelEvent()
    data object DeleteSuccess: ItemDetailViewModelEvent()

}
