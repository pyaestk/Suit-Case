package com.project.suitcase.data.utils

import com.project.suitcase.data.model.UserDetailResponse
import com.project.suitcase.domain.model.UserDetailModel

fun UserDetailResponse.toModels(): UserDetailModel = UserDetailModel(
    phoneNumber = this.phoneNumber.orEmpty(),
    name = this.name.orEmpty(),
    email = this.email.orEmpty(),
    userImage = this.userImage.orEmpty()
)