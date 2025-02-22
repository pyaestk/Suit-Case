package com.project.suitcase.data.repository

import com.project.suitcase.data.datasource.AuthRemoteDatasource
import com.project.suitcase.data.utils.toModels
import com.project.suitcase.domain.model.UserDetailModel

class AuthRepository(
    private val authRemoteDatasource: AuthRemoteDatasource
) {

    suspend fun registerAccount(
        username: String,
        password: String,
        email: String,
        phoneNumber: String
    ): Result<Unit>{

        val result = authRemoteDatasource.registerAccount(
            userName = username,
            email = email,
            password = password,
            phoneNumber = phoneNumber
        ).map {

        }
        
        return result
    }

    suspend fun loginAccount(
        email: String,
        password: String
    ): Result<Unit> {
        val result = authRemoteDatasource.loginAccount(
            email = email,
            password = password
        ).map {

        }

        return result
    }

    suspend fun getUserInfo(): Result<UserDetailModel>{
        return authRemoteDatasource.getUserInfo().map {
            it.toModels()
        }
    }

}