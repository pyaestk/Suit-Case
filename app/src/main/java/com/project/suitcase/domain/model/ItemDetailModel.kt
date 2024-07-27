package com.project.suitcase.domain.model

data class ItemDetailModel(
    val itemId: String,
    val itemName: String,
    val itemDescription: String,
    val itemLocation: String,
    val itemImage: String,
    val itemPrice: String,
    val tripId: String,
    val finished: Boolean?,
)
