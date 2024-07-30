package com.project.suitcase.data.utils

import com.project.suitcase.data.model.ItemResponse
import com.project.suitcase.domain.model.ItemDetailModel

fun ItemResponse.toModels(): ItemDetailModel = ItemDetailModel(
    itemId = this.itemId,
    itemImage = this.itemImage.toString(),
    itemLocation = this.itemLocation,
    itemPrice = this.itemPrice,
    itemName = this.itemName,
    itemDescription = this.itemDescription,
    tripId = this.tripId,
    finished = this.finished
)

fun List<ItemResponse>.toModels(): List<ItemDetailModel> = this.map {
    it.toModels()
}