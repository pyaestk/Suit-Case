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
    private val tripRepository: TripRepository,
    private val itemRepository: ItemRepository
): ViewModel() {

    private val _uiState = MutableLiveData<ItemDetailUiState>()
    val uiState: LiveData<ItemDetailUiState> = _uiState

    private val _itemDetailUiEvent = SingleLiveEvent<ItemDetailViewModelEvent>()
    val itemDetailUiEvent: LiveData<ItemDetailViewModelEvent> = _itemDetailUiEvent

    private val _editItemDetailUiEvent = SingleLiveEvent<EditItemDetailViewModelEvent>()
    val editItemDetailUiEvent: LiveData<EditItemDetailViewModelEvent> = _editItemDetailUiEvent

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
                    _editItemDetailUiEvent.value = EditItemDetailViewModelEvent.Success
                },
                onFailure = {
                    _editItemDetailUiEvent.value = EditItemDetailViewModelEvent.Error(
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
}

sealed class EditItemDetailViewModelEvent() {
    data object Success :EditItemDetailViewModelEvent()
    data class Error(val error: String) :EditItemDetailViewModelEvent()
}