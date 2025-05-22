package com.project.suitcase.views.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.domain.model.UserDetailModel
import com.project.suitcase.domain.repository.AuthRepository
import com.project.suitcase.views.viewmodel.util.SingleLiveEvent
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _uiState = MutableLiveData<UserProfileUiState>()
    val uiState: LiveData<UserProfileUiState> = _uiState

    private val _userProfileEvent = SingleLiveEvent<UserProfileViewModelEvent>()
    val userProfileEvent: LiveData<UserProfileViewModelEvent> = _userProfileEvent

    fun getUserDetail(){
        viewModelScope.launch {
            authRepository.getUserInfo().fold(
                onFailure = {
                    _userProfileEvent.value = UserProfileViewModelEvent.Error(
                        it.message ?: "Something went wrong"
                    )
                },
                onSuccess = {
                    _uiState.value = UserProfileUiState.Success(it)
                }
            )
        }
    }

    fun updateUserInfo(
        name: String,
        phoneNumber: String,
        userImage: Uri?
    ){
        _uiState.value = UserProfileUiState.Loading
        viewModelScope.launch {
            authRepository.updateUserInfo(
                userImage = userImage,
                name = name,
                phoneNumber = phoneNumber
            ).fold(
                onSuccess = {
                    _uiState.value = UserProfileUiState.UpdateSuccess
                },
                onFailure = {
                    _userProfileEvent.value = UserProfileViewModelEvent.Error(
                        it.message ?: "Something went wrong"
                    )
                }
            )
        }
    }

}
sealed class UserProfileUiState {
    data object Loading : UserProfileUiState()
    data class Success(val userDetail: UserDetailModel) : UserProfileUiState()
    data object UpdateSuccess: UserProfileUiState()
}
sealed class UserProfileViewModelEvent {
    data class Error(val error: String) :UserProfileViewModelEvent()
}
