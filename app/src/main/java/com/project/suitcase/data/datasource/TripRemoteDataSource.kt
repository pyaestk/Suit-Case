package com.project.suitcase.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.project.suitcase.data.model.ItemResponse
import com.project.suitcase.data.model.TripResponse
import kotlinx.coroutines.tasks.await

class TripRemoteDataSource(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) {

    suspend fun addTrip(
        tripName: String,
        date: String
    ): Result<String> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val docRef = fireStore.collection("users").document(user.uid)
                    .collection("trip").document()
                val tripId = docRef.id

                val tripInfo = TripResponse(
                    tripId = tripId,
                    tripName = tripName,
                    date = date,
                    items = emptyList()
                )

                docRef.set(tripInfo)
                Result.success(tripId)

            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTripsAndItems(): Result<List<TripResponse>> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val snapshot = fireStore.collection("users").document(user.uid)
                    .collection("trip").orderBy("date").get().await()

                val trips = snapshot.documents.mapNotNull { tripDoc ->
                    val trip = tripDoc.toObject<TripResponse>()
                    trip?.let {
                        val itemsSnapshot = tripDoc.reference.collection("items")
                            .orderBy("finished").get().await()
                        val items = itemsSnapshot.documents.mapNotNull { it.toObject<ItemResponse>() }
                        trip.copy(items = items)
                    }
                }
                Result.success(trips)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrips(): Result<List<TripResponse>> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val snapshot = fireStore.collection("users").document(user.uid)
                    .collection("trip").orderBy("date") .get().await()
                val trips = snapshot.documents.mapNotNull {
                    it.toObject<TripResponse>()
                }
                Result.success(trips)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
