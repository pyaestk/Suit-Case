package com.project.suitcase.data.datasource

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.project.suitcase.data.model.UserDetailResponse
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AuthRemoteDatasource(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val fStorage: FirebaseStorage
) {
    suspend fun registerAccount(
        userName: String,
        password: String,
        email: String,
        phoneNumber: String
    ): Result<String> {
        return try {
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()
            val user = authResult.user
            if (user != null) {
                val userInfo = UserDetailResponse(
                    email = email,
                    phoneNumber = phoneNumber,
                    name = userName,
                    userImage = null
                )
                firestore.collection("users")
                    .document(user.uid)
                    .set(userInfo).await()

                Result.success(user.uid)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun loginAccount(email: String, password: String): Result<String> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Login failed")
            Result.success(user.uid)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserInfo(): Result<UserDetailResponse> {
        return try {
            val uid = firebaseAuth.currentUser!!.uid
            val documentSnapshot = firestore.collection("users").document(uid).get().await()
            val userResponse = documentSnapshot.toObject<UserDetailResponse>()

            if (userResponse != null) {
                Result.success(userResponse)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserDetail(
        name: String,
        phoneNumber: String,
        userImage: Uri?
    ): Result<Unit> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("User not authenticated"))

            val updates = mutableMapOf<String, Any?>(
                "name" to name,
                "phoneNumber" to phoneNumber
            )

            if (userImage != null) {
                val userDoc = firestore.collection("users").document(uid).get().await()
                val previousImageUrl = userDoc.getString("userImage")

                previousImageUrl?.let { url ->
                    try {
                        val ref = fStorage.getReferenceFromUrl(url)
                        ref.delete().await()
                    } catch (_: Exception) {

                    }
                }

                val imageId = UUID.randomUUID().toString()
                val imageRef = fStorage.reference.child("profiles/$imageId")
                val uploadTask = imageRef.putFile(userImage).await()
                if (uploadTask.task.isSuccessful) {
                    val newImageUrl = imageRef.downloadUrl.await().toString()
                    updates["userImage"] = newImageUrl
                } else {
                    return Result.failure(Exception("Failed to upload image"))
                }
            }

            firestore.collection("users")
                .document(uid)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}