package com.project.suitcase.data.repository

import android.net.Uri
import com.project.suitcase.data.datasource.ItemRemoteDatasource
import com.project.suitcase.data.utils.toModels
import com.project.suitcase.domain.model.ItemDetailModel

class ItemRepository(
    private val itemRemoteDatasource: ItemRemoteDatasource
) {

    suspend fun addItem(
        tripId: String,
        itemName: String,
        itemDescription: String,
        itemLocation: String,
        itemImage: String,
        itemPrice: String,
        finished: Boolean
    ): Result<String> {
        val result = itemRemoteDatasource.addItem(
            tripId = tripId,
            itemPrice = itemPrice,
            itemDescription = itemDescription,
            itemLocation = itemLocation,
            itemImage = itemImage,
            itemName = itemName,
            finished = finished
        )
        return result
    }

    suspend fun getItems(
        tripId: String
    ): Result<List<ItemDetailModel>> {
        val result = itemRemoteDatasource.getItems(
            tripId = tripId
        ).map {
            it.toModels()
        }
        return result
    }

    suspend fun updateCheckedItemStatus(
        tripId: String,
        itemId: String,
        finished: Boolean
    ): Result<Unit> {
        return itemRemoteDatasource.updateItemCheckedStatus(
            itemId = itemId,
            finished = finished,
            tripId = tripId
        )
    }

    suspend fun uploadImage(imageUri: Uri): Result<String> {
        val result = itemRemoteDatasource.uploadImage(imageUri)
        return result
    }


}