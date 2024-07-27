package com.project.suitcase.data.model

data class TripResponse(
    val tripId: String = "",
    val tripName: String = "",
    val date: String = "",
    val items: List<ItemResponse> = emptyList()
)