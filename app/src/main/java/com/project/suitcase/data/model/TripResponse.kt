package com.project.suitcase.data.model

import com.google.firebase.Timestamp

data class TripResponse(
    val tripId: String? = null,
    val tripName: String? = null,
    val date: Timestamp? = null,
    val items: List<ItemResponse> = emptyList()
) {
    // No-argument constructor for Firestore
    constructor() : this(null, null, null, emptyList())
} 