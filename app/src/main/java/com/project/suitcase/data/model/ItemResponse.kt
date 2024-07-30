package com.project.suitcase.data.model

data class ItemResponse(
    val itemId: String ="",
    val itemName: String = "",
    val itemDescription: String= "",
    val itemLocation: String= "",
    val itemImage: String? = null,
    val itemPrice: String= "",
    val tripId: String= "",
    val finished: Boolean? = false
)
