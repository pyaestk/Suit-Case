package com.project.suitcase.data.model

data class TripResponse(
    val tripId: String? = null,
    val tripName: String? = null,
    val date: String? = null,
    val items: List<ItemResponse> = emptyList()
) {
    // No-argument constructor for Firestore
    constructor() : this(null, null, null, emptyList())
}