package com.project.suitcase.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.suitcase.data.model.RegisterResponse
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
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User registration failed")

            val userInfo = RegisterResponse(
                data = UserDetailResponse(
                    email = email,
                    password = password,
                    phoneNumber = phoneNumber,
                    name = userName
                )
            )
            firestore.collection("users").document(user.uid).set(userInfo).await()

            Result.success(user.uid)

        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun loginAccount(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Login failed")
            Result.success(user.uid)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}