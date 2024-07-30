package com.project.suitcase.data.repository

import android.util.Log
import com.project.suitcase.data.datasource.TripRemoteDataSource
import com.project.suitcase.data.utils.toModels
import com.project.suitcase.domain.model.TripDetailModel

class TripRepository(
    private val tripRemoteDataSource: TripRemoteDataSource
) {

    suspend fun addTrip(
        tripName: String,
        date: String
    ): Result<String> {
        val result = tripRemoteDataSource.addTrip(
            tripName = tripName,
            date = date
        )
        return result
    }

    suspend fun getTripsIncludingItems(): Result<List<TripDetailModel>>{
        val result = tripRemoteDataSource.getTripsIncludingItems().map { tripResponseList ->
            tripResponseList.toModels()
        }
        Log.i("TripRepo", result.toString())
        return result
    }
    suspend fun getTrips(): Result<List<TripDetailModel>>{
        return tripRemoteDataSource.getTrips().map { it.toModels() }
    }
    suspend fun deleteAllTrip(): Result<Unit> {
        return tripRemoteDataSource.deleteAllTrip()
    }
}