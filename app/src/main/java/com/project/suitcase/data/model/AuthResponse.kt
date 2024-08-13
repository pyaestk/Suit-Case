package com.project.suitcase.data.model


data class UserDetailResponse(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val phoneNumber: String? = null,
    val userImage: String? = null
) {
    constructor() : this(
        null,
        null,
        null,
        null,
        null
    )
}