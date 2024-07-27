package com.project.suitcase.view.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.data.repository.ItemRepository
import com.project.suitcase.domain.model.ItemDetailModel
import com.project.suitcase.view.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ItemViewModel(
   private val itemRepository: ItemRepository
): ViewModel() {

    private val _uiState = MutableLiveData<ItemUiState>()
    val uiState: LiveData<ItemUiState> = _uiState

    private val _addItemUiEvent = SingleLiveEvent<ItemViewModelEvent>()
    val addItemUiEvent: LiveData<ItemViewModelEvent> = _addItemUiEvent

    private val _imageUiState = MutableLiveData<ImageUiState>()
    val imageUiState: LiveData<ImageUiState> = _imageUiState
    private val _imageUiEvent = SingleLiveEvent<ImageUIViewModelEvent>()
    val imageUiEvent: LiveData<ImageUIViewModelEvent> = _imageUiEvent

    private var _itemListUiEvent = SingleLiveEvent<ItemListViewModelEvent>()
    val itemListUiEvent: LiveData<ItemListViewModelEvent> = _itemListUiEvent

    private var _itemCheckedUiEvent = SingleLiveEvent<ItemCheckedViewModelEvent>()
    val itemCheckedUiEvent: LiveData<ItemCheckedViewModelEvent> = _itemCheckedUiEvent



    fun addItem(
        tripId: String,
        itemName: String,
        itemDescription: String,
        itemLocation: String,
        itemImage: String,
        itemPrice: String
    ) {
        _uiState.value = ItemUiState.Loading
        viewModelScope.launch {
            itemRepository.addItem(
                tripId = tripId,
                itemPrice = itemPrice,
                itemDescription = itemDescription,
                itemLocation = itemLocation,
                itemImage = itemImage,
                itemName = itemName,
                finished = false
            ).fold(
                onSuccess = {itemId ->
                    _addItemUiEvent.value = ItemViewModelEvent.Success(itemId)
                },
                onFailure = {
                    _addItemUiEvent.value = ItemViewModelEvent.Error(it.message?:
                    "Something went wrong")
                }
            )
        }

    }

    fun uploadImage(imageUri: Uri){
        _imageUiState.value = ImageUiState.Loading
        viewModelScope.launch {
            itemRepository.uploadImage(imageUri).fold(
                onSuccess = {
                    _imageUiEvent.value = ImageUIViewModelEvent.Success(it)
                },
                onFailure = {
                    _imageUiEvent.value = ImageUIViewModelEvent.Error(
                        it.message ?: "Something went wrong"
                    )
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
            ).fold(
                onSuccess = {
                    _itemCheckedUiEvent.value = ItemCheckedViewModelEvent.Success
                },
                onFailure = {
                    _itemCheckedUiEvent.value = ItemCheckedViewModelEvent.
                            Error(it.message ?: " Something went wrong")
                }
            )
        }
    }

    fun getItems(tripId: String) {
        _uiState.value = ItemUiState.Loading
        viewModelScope.launch {
            itemRepository.getItems(
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


}

sealed class ItemUiState {
    data object Loading : ItemUiState()
}

sealed class ItemViewModelEvent {
    data class Success(val itemId: String): ItemViewModelEvent()
    data class Error(val error: String): ItemViewModelEvent()
}

sealed class ItemListViewModelEvent {
    data class Success(val itemList: List<ItemDetailModel>) : ItemListViewModelEvent()
    data class Error(val error: String) : ItemListViewModelEvent()
}

sealed class ItemCheckedViewModelEvent {
    data object Success: ItemCheckedViewModelEvent()
    data class Error(val error: String) : ItemCheckedViewModelEvent()
}

sealed class ImageUiState{
    data object Loading: ImageUiState()
}

sealed class ImageUIViewModelEvent{
    data class Success(val imageUri: String): ImageUIViewModelEvent()
    data class Error(val error: String): ImageUIViewModelEvent()
}