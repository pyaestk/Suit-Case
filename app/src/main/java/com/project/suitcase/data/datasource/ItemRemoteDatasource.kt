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

    suspend fun addItem(
        tripId: String,
        itemName: String,
        itemDescription: String,
        itemLocation: String,
        itemImage: String,
        itemPrice: String,
        finished: Boolean
    ): Result<String> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val docRef = fireStore.collection("users").document(user.uid)
                    .collection("trip").document(tripId)
                    .collection("items").document()
                val itemId = docRef.id

                val itemInfo = ItemResponse(
                    itemId = itemId,
                    itemPrice = itemPrice,
                    itemDescription = itemDescription,
                    itemLocation = itemLocation,
                    itemImage = itemImage,
                    itemName = itemName,
                    tripId = tripId,
                    finished = finished
                )

                docRef.set(itemInfo)
                Result.success(itemId)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadImage(imageUri: Uri): Result<String> {
        return try {
            val imageId = UUID.randomUUID().toString()
            val imageRef = fStorage.reference.child("items/$imageId")
            val uploadTask = imageRef.putFile(imageUri).await()
            if (uploadTask.task.isSuccessful) {
                val downloadUrl = imageRef.downloadUrl.await()
                Result.success(downloadUrl.toString())
            } else {
                Result.failure(uploadTask.task.exception ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateItemCheckedStatus(tripId: String,itemId: String, finished: Boolean): Result<Unit>{
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val itemRef = fireStore.collection("users").document(user.uid)
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

    suspend fun getItems(tripId: String): Result<List<ItemResponse>>{
        return try {
            val user = firebaseAuth.currentUser
            if (user != null){
                val snapshot = fireStore.collection("users").document(user.uid)
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
}