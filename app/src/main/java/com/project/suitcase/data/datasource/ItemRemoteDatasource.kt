package com.project.suitcase.data.datasource

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.project.suitcase.data.model.ItemResponse
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ItemRemoteDatasource(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val fStorage: FirebaseStorage
) {
    //item adding
    suspend fun addItem(
        tripId: String,
        tripName: String,
        itemName: String,
        itemDescription: String,
        itemLocation: String,
        itemImage: Uri?,
        itemPrice: String,
        finished: Boolean,
    ): Result<String> {

        return try {
            val user = firebaseAuth.currentUser
            val userRef = fireStore.collection("users").document(user!!.uid)
            if (user != null) {

                val docRef = userRef.collection("trip").document(tripId)
                    .collection("items").document()

                val itemId = docRef.id

                val itemImageUrl = if (itemImage != null) {
                    val imageId = UUID.randomUUID().toString()
                    val imageRef = fStorage.reference.child("items/$imageId")
                    val uploadTask = imageRef.putFile(itemImage).await()
                    if (uploadTask.task.isSuccessful) {
                        imageRef.downloadUrl.await().toString()
                    } else {
                        null
                    }
                } else {
                    null
                }
                
                val itemInfo = ItemResponse(
                    itemId = itemId,
                    itemPrice = itemPrice,
                    itemDescription = itemDescription,
                    itemLocation = itemLocation,
                    itemImage = itemImageUrl,
                    itemName = itemName,
                    tripId = tripId,
                    tripName = tripName,
                    finished = finished
                )
                docRef.set(itemInfo).await()
                Result.success(itemId)

            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update item details
    suspend fun editItemDetail(
        tripId: String,
        itemId: String,
        itemName: String?,
        itemDescription: String?,
        itemLocation: String?,
        itemImage: Uri?,
        itemPrice: String?,
    ): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val itemRef = fireStore.collection("users")
                    .document(user.uid)
                    .collection("trip").document(tripId)
                    .collection("items").document(itemId)

                val updates = mutableMapOf<String, Any?>()

                // Add fields to be updated
                itemName?.let { updates["itemName"] = it }
                itemDescription?.let { updates["itemDescription"] = it }
                itemLocation?.let { updates["itemLocation"] = it }
                itemPrice?.let { updates["itemPrice"] = it }

                // Update item image if provided
                if (itemImage != null) {
                    val itemDoc = itemRef.get().await()
                    val previousImageUrl = itemDoc.getString("itemImage")

                    previousImageUrl?.let { url ->
                        try {
                            val ref = fStorage.getReferenceFromUrl(url)
                            ref.delete().await()
                        } catch (_: Exception) {

                        }
                    }

                    val imageId = UUID.randomUUID().toString()
                    val imageRef = fStorage.reference.child("items/$imageId")
                    val uploadTask = imageRef.putFile(itemImage).await()
                    if (uploadTask.task.isSuccessful) {
                        val itemImageUrl = imageRef.downloadUrl.await().toString()
                        updates["itemImage"] = itemImageUrl
                    } else {
                        return Result.failure(Exception("Failed to upload image"))
                    }
                }

                // Perform the update
                if (updates.isNotEmpty()) {
                    itemRef.update(updates).await()
                }

                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    //getting items by trip
    suspend fun getItemsByTrip(tripId: String): Result<List<ItemResponse>>{
        return try {
            val user = firebaseAuth.currentUser
            val userRef = fireStore.collection("users").document(user!!.uid)
            if (user != null){
                val snapshot = userRef
                    .collection("trip").document(tripId)
                    .collection("items").get().await()
                val items = snapshot.documents.mapNotNull {
                    it.toObject<ItemResponse>()
                }
                Result.success(items)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //get all items from every trips
    suspend fun getAllItems(): Result<List<ItemResponse>> {
        return try {
            val user = firebaseAuth.currentUser

            if (user != null) {
                val tripsSnapshot = fireStore.collection("users")
                    .document(user.uid)
                    .collection("trip")
                    .get().await()

                val allItems = mutableListOf<ItemResponse>()

                for (tripDoc in tripsSnapshot.documents) {
                    val itemsSnapshot = tripDoc.reference.collection("items")
                        .orderBy("finished")
                        .get()
                        .await()

                    val items = itemsSnapshot.documents.mapNotNull { it.toObject<ItemResponse>() }
                    allItems.addAll(items)
                }

                Result.success(allItems)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //get item's details
    suspend fun getItemDetails(tripId: String, itemId: String): Result<ItemResponse> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val itemSnapshot = fireStore.collection("users")
                    .document(user.uid)
                    .collection("trip").document(tripId)
                    .collection("items").document(itemId)
                    .get()
                    .await()

                val item = itemSnapshot.toObject<ItemResponse>()
                if (item != null) {
                    Result.success(item)
                } else {
                    Result.failure(Exception("Item not found"))
                }
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    //get all finished items under every trip
    suspend fun deleteAllFinishedItems(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val tripsSnapshot = fireStore.collection("users")
                    .document(user.uid)
                    .collection("trip")
                    .get().await()

                for (tripDoc in tripsSnapshot.documents) {
                    val itemsSnapshot = tripDoc.reference.collection("items")
                        .whereEqualTo("finished", true)
                        .get()
                        .await()

                    for (itemDoc in itemsSnapshot.documents) {
                        itemDoc.reference.delete().await()
                    }
                }

                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //get all finished items under every trip
    suspend fun getAllFinishedItems(): Result<List<ItemResponse>> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val tripsSnapshot = fireStore.collection("users")
                    .document(user.uid)
                    .collection("trip")
                    .get().await()

                val finishedItems = mutableListOf<ItemResponse>()

                for (tripDoc in tripsSnapshot.documents) {
                    val itemsSnapshot = tripDoc.reference.collection("items")
                        .whereEqualTo("finished", true)
                        .get()
                        .await()

                    val items = itemsSnapshot.documents.mapNotNull { it.toObject<ItemResponse>() }
                    finishedItems.addAll(items)
                }

                Result.success(finishedItems)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //delete item by itemId, tripId
    suspend fun deleteItem(tripId: String, itemId: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            val userRef = fireStore.collection("users").document(user!!.uid)
            if (user != null) {
                val itemRef = userRef
                    .collection("trip").document(tripId)
                    .collection("items").document(itemId)

                itemRef.delete().await()

                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //item updating under trip
    suspend fun updateItemCheckedStatus(tripId: String, itemId: String, finished: Boolean): Result<Unit>{
        return try {
            val user = firebaseAuth.currentUser
            val userRef = fireStore.collection("users").document(user!!.uid)
            if (user != null) {
                val itemRef = userRef
                    .collection("trip").document(tripId)
                    .collection("items").document(itemId)
                itemRef.update("finished", finished).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Mark all items as finished for a specific trip
    suspend fun markAllItemsAsFinished(tripId: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val userRef = fireStore.collection("users").document(user.uid)
                val itemsRef = userRef.collection("trip").document(tripId).collection("items")

                // Get all items in the trip
                val snapshot = itemsRef.get().await()

                val batch = fireStore.batch()

                // Mark each item as finished
                for (document in snapshot.documents) {
                    batch.update(document.reference, "finished", true)
                }

                // Commit the batch update
                batch.commit().await()

                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    //delete all items in specific trip
    suspend fun deleteAllItemsInTrip(tripId: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            val userRef = fireStore.collection("users").document(user!!.uid)
            if (user != null) {
                val itemsRef = userRef
                    .collection("trip").document(tripId)
                    .collection("items")

                val snapshot = itemsRef.get().await()
                val batch = fireStore.batch()

                snapshot.documents.forEach { document ->
                    batch.delete(document.reference)
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



}