package com.project.suitcase.data.utils

import com.project.suitcase.data.model.ItemResponse
import com.project.suitcase.data.model.TripResponse
import com.project.suitcase.domain.model.ItemDetailModel
import com.project.suitcase.domain.model.TripDetailModel

fun TripResponse.toModels(): TripDetailModel = TripDetailModel(
    tripName = this.tripName,
    date = this.date,
    tripId = this.tripId,
    items = this.items.toModels()
)

fun List<TripResponse>.toModels(): List<TripDetailModel> = this.map {
    it.toModels()
}




