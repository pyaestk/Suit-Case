package com.project.suitcase.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.project.suitcase.data.repository.AuthRepository
import com.project.suitcase.view.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _uiState = MutableLiveData<LoginUiState>()
    val uiState: LiveData<LoginUiState> = _uiState

    private val _uiEvent = SingleLiveEvent<LoginViewModelEvent>()
    val uiEvent: LiveData<LoginViewModelEvent> = _uiEvent

    init {
        val fAuth = FirebaseAuth.getInstance()
        val user = fAuth.currentUser
        if (user != null){
            _uiState.value = LoginUiState.NavigateToMainScreen
        }
    }

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

    data object NavigateToMainScreen: LoginUiState()
}

sealed class LoginViewModelEvent {
    data object LoginSuccess : LoginViewModelEvent()
    data class Error(val error: String) : LoginViewModelEvent()
}