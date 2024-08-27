package com.project.suitcase.data.utils

import com.project.suitcase.data.model.ItemResponse
import com.project.suitcase.domain.model.ItemDetailModel

fun ItemResponse.toModels(): ItemDetailModel = ItemDetailModel(
    itemId = this.itemId.orEmpty(),
    itemImage = this.itemImage.orEmpty(),
    itemLocation = this.itemLocation.orEmpty(),
    itemPrice = this.itemPrice.orEmpty(),
    itemName = this.itemName.orEmpty(),
    itemDescription = this.itemDescription.orEmpty(),
    tripId = this.tripId.orEmpty(),
    finished = this.finished,
    tripName = this.tripName.orEmpty()
)

fun List<ItemResponse>.toModels(): List<ItemDetailModel> = this.map {
    it.toModels()
}