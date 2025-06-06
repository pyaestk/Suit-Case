package com.project.suitcase.domain.repository

import android.net.Uri
import com.project.suitcase.data.datasource.ItemRemoteDatasource
import com.project.suitcase.data.utils.toModels
import com.project.suitcase.domain.model.ItemDetailModel

class ItemRepository(
    private val itemRemoteDatasource: ItemRemoteDatasource
) {

    suspend fun addItem(
        tripId: String,
        tripName: String,
        itemName: String,
        itemDescription: String,
        itemLocation: String,
        itemImage: Uri?,
        itemPrice: String,
        finished: Boolean,
    ): Result<String> {
        val result = itemRemoteDatasource.addItem(
            tripId = tripId,
            tripName = tripName,
            itemPrice = itemPrice,
            itemDescription = itemDescription,
            itemLocation = itemLocation,
            itemImage = itemImage,
            itemName = itemName,
            finished = finished,
        )
        return result
    }

    suspend fun editItem(
        tripId: String,
        itemId: String,
        itemName: String?,
        itemDescription: String?,
        itemLocation: String?,
        itemImage: Uri?,
        itemPrice: String?,
    ): Result<Unit> {

        return itemRemoteDatasource.editItemDetail(
            tripId = tripId,
            itemId = itemId,
            itemPrice = itemPrice,
            itemDescription = itemDescription,
            itemLocation = itemLocation,
            itemImage = itemImage,
            itemName = itemName,
        )

    }

    suspend fun getItemsByTrip(
        tripId: String
    ): Result<List<ItemDetailModel>> {
        val result = itemRemoteDatasource.getItemsByTrip(
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

    suspend fun markAllItemsAsFinished(tripId: String): Result<Unit> {
        return itemRemoteDatasource.markAllItemsAsFinished(tripId)
    }

    suspend fun deleteAllItems(tripId: String): Result<Unit> {
        val result = itemRemoteDatasource.deleteAllItemsInTrip(tripId)
        return result
    }

    suspend fun deleteItem(tripId: String, itemId: String): Result<Unit> {
        return itemRemoteDatasource.deleteItem(tripId = tripId, itemId = itemId)
    }

    suspend fun getAllItems(): Result<List<ItemDetailModel>> {
        val result = itemRemoteDatasource.getAllItems().map {
            it.toModels()
        }
        return result
    }

    suspend fun getAllFinishedItems(): Result<List<ItemDetailModel>>{
        val result = itemRemoteDatasource.getAllFinishedItems().map {
            it.toModels()
        }
        return result
    }

    suspend fun deleteAllFinishedItems(): Result<Unit> {
        val result = itemRemoteDatasource.deleteAllFinishedItems()
        return result
    }

    suspend fun getItemDetails(tripId: String, itemId: String): Result<ItemDetailModel>{
        return itemRemoteDatasource.getItemDetails(tripId = tripId, itemId = itemId).map {
            it.toModels()
        }
    }
}