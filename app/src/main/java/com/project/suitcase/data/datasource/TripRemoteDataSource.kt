package com.project.suitcase.data.datasource

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.project.suitcase.data.model.ItemResponse
import com.project.suitcase.data.model.TripResponse
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

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
                    date = parseDateStringToTimestamp(date)
                )
                docRef.set(tripInfo).await()
                Result.success(tripId)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseDateStringToTimestamp(dateString: String): Timestamp? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.parse(dateString)
            date?.let { Timestamp(it) }
        } catch (e: Exception) {
            Log.e("DateParse", "Failed to parse date string: $dateString", e)
            null
        }
    }

    suspend fun getTripsIncludingItems(): Result<List<TripResponse>> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val snapshot = fireStore.collection("users")
                    .document(user.uid)
                    .collection("trip")
                    .orderBy("date")
                    .get().await()

                val trips = snapshot.documents.mapNotNull { tripDoc ->
                    val trip = tripDoc.toObject<TripResponse>()
                    trip?.let {
                        val itemsSnapshot = tripDoc.reference.collection("items")
                            .get().await()
                        val items = itemsSnapshot.documents.mapNotNull {
                            it.toObject<ItemResponse>()
                        }
                        trip.copy(items = items)
                    }
                }
                Log.e("TripRemoteData", "${user}")

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

                val snapshot = fireStore.collection("users")
                    .document(user.uid)
                    .collection("trip")
                    .orderBy("date")
                    .get().await()

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

    suspend fun deleteAllTrip(): Result<Unit>{
        return try{
            val user = firebaseAuth.currentUser
            if (user != null) {
                val snapshot = fireStore.collection("users")
                    .document(user.uid)
                    .collection("trip").get().await()

                val batch = fireStore.batch()

                snapshot.documents.forEach{
                    batch.delete(it.reference)
                }
                batch.commit().await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)

        }
    }

    suspend fun deleteTrip(tripId: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val tripRef = fireStore.collection("users")
                    .document(user.uid)
                    .collection("trip")
                    .document(tripId)

                tripRef.delete().await()

                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun editTrip(
        tripId: String,
        tripName: String? = null,
        date: String? = null
    ): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val tripRef = fireStore.collection("users")
                    .document(user.uid)
                    .collection("trip")
                    .document(tripId)

                val updates = mutableMapOf<String, Any>()
                tripName?.let { updates["tripName"] = it }
                date?.let {
                    val parsedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it)
                    parsedDate?.let { dateObj -> updates["date"] = Timestamp(dateObj) }
                }

                if (updates.isNotEmpty()) {
                    tripRef.update(updates).await()
                }

                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
