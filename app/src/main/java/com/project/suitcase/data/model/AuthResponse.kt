package com.project.suitcase.data.model


data class RegisterResponse(
    val data: UserDetailResponse
)
data class UserDetailResponse(
    val name: String?,
    val email: String?,
    val password: String?,
    val phoneNumber: String?
)