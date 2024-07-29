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
    val user = firebaseAuth.currentUser
    val userRef = fireStore.collection("users").document(user!!.uid)

    suspend fun addItem(
        tripId: String,
        itemName: String,
        itemDescription: String,
        itemLocation: String,
        itemImage: Uri,
        itemPrice: String,
        finished: Boolean,
    ): Result<String> {
        return try {
            if (user != null) {
                val docRef = userRef.collection("trip").document(tripId)
                    .collection("items").document()

                val itemId = docRef.id

                //image upload
                val imageId = UUID.randomUUID().toString()
                val imageRef = fStorage.reference.child("items/$imageId")
                val uploadTask = imageRef.putFile(itemImage).await()
                if (uploadTask.task.isSuccessful) {
                    val downloadUrl = imageRef.downloadUrl.await()
                    val itemInfo = ItemResponse(
                        itemId = itemId,
                        itemPrice = itemPrice,
                        itemDescription = itemDescription,
                        itemLocation = itemLocation,
                        itemImage = downloadUrl.toString(),
                        itemName = itemName,
                        tripId = tripId,
                        finished = finished
                    )
                    docRef.set(itemInfo).await()
                    Result.success(itemId)
                } else {
                    Result.failure(uploadTask.task.exception ?: Exception("Unknown error"))
                }
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateItemCheckedStatus(tripId: String,itemId: String, finished: Boolean): Result<Unit>{
        return try {
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

    suspend fun getItemsByTrip(tripId: String): Result<List<ItemResponse>>{
        return try {
            if (user != null){
                val snapshot = userRef
                    .collection("trip").document(tripId)
                    .collection("items").orderBy("finished").get().await()
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

    suspend fun getFinishedItemList(): Result<List<ItemResponse>>{
        return try {
            if (user!=null) {
                val snapshot = userRef
                    .collection("finished_items").get().await()
                val items = snapshot.documents.mapNotNull {
                    it.toObject<ItemResponse>()
                }
                Result.success(items)
            }else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteItem(tripId: String, itemId: String): Result<Unit> {
        return try {
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

    suspend fun moveToFinished(tripId: String, itemId: String): Result<Unit> {
        return try {
            if (user != null) {
                val itemRef = userRef
                    .collection("trip").document(tripId)
                    .collection("items").document(itemId)

                val finishedItemRef = fireStore.collection("users").document(user.uid)
                    .collection("finished_items").document(itemId)

                val itemSnapshot = itemRef.get().await()
                if (itemSnapshot.exists()) {
                    val item = itemSnapshot.toObject<ItemResponse>()
                    if (item != null) {
                        // Add item to the finished collection
                        finishedItemRef.set(item).await()
                        // Remove item from the original collection
                        itemRef.delete().await()
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Item not found"))
                    }
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

    suspend fun removeFromFinished(itemId: String): Result<Unit> {
        return try{
            if (user != null ) {
                val finishedItemRef = userRef
                    .collection("finished_items").document(itemId)

                finishedItemRef.delete().await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllItemsInTrip(tripId: String): Result<Unit> {
        return try {
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