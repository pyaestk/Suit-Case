package com.project.suitcase.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.domain.repository.AuthRepository
import com.project.suitcase.domain.model.UserDetailModel
import com.project.suitcase.ui.viewmodel.util.SingleLiveEvent
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

}
sealed class UserProfileUiState {
    data object Loading : UserProfileUiState()
    data class Success(val userDetail: UserDetailModel) : UserProfileUiState()
}
sealed class UserProfileViewModelEvent {
    data class Error(val error: String) :UserProfileViewModelEvent()
}
