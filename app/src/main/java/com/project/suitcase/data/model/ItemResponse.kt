package com.project.suitcase.data.model

data class ItemResponse(
    val itemId: String? = null,
    val itemName: String? = null,
    val itemDescription: String? = null,
    val itemLocation: String? = null,
    val itemImage: String? = null,
    val itemPrice: String? = null,
    val tripId: String? = null,
    val finished: Boolean? = false
) {
    // No-argument constructor for Firestore
    constructor() : this(null,
        null,
        null,
        null,
        null,
        null,
        null,
        false)
}
