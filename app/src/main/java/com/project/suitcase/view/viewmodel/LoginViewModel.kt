package com.project.suitcase.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _uiState = MutableLiveData<LoginUiState>()
    val uiState: LiveData<LoginUiState> = _uiState

    private val _uiEvent = MutableLiveData<LoginViewModelEvent>()
    val uiEvent: LiveData<LoginViewModelEvent> = _uiEvent

    fun login(
        email: String,
        password: String
    ) {

        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            authRepository.loginAccount(
                email = email,
                password = password,
            ).fold(
                onSuccess = {
                    _uiEvent.value = LoginViewModelEvent.LoginSuccess
                },
                onFailure = {
                    _uiEvent.value = LoginViewModelEvent.Error(
                        it.message?: "Something went wrong"
                    )
                }
            )
        }
    }

}

sealed class LoginUiState {
    data object Loading : LoginUiState()
}

sealed class LoginViewModelEvent {
    data object LoginSuccess : LoginViewModelEvent()
    data class Error(val error: String) : LoginViewModelEvent()
}