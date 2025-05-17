package com.project.suitcase.domain.model

data class TripDetailModel(
    val tripId: String,
    val tripName: String,
    val date: String,
    val items: List<ItemDetailModel>,
    var isExpanded: Boolean = false
)