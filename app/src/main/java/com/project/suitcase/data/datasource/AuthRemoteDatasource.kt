package com.project.suitcase.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.project.suitcase.data.model.UserDetailResponse
import kotlinx.coroutines.tasks.await

class AuthRemoteDatasource(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
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
                    password = password,
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
        userImage: String
    ): Result<Unit> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("User not authenticated"))

            val updates = mapOf(
                "name" to name,
                "phoneNumber" to phoneNumber,
                "userImage" to userImage
            )

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