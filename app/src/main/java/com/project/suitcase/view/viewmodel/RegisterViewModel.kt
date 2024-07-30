package com.project.suitcase.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.data.repository.AuthRepository
import com.project.suitcase.view.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _uiState = MutableLiveData<RegisterUiState>()
    val uiState: LiveData<RegisterUiState> = _uiState

    private val _uiEvent = SingleLiveEvent<RegisterViewModelEvent>()
    val uiEvent: LiveData<RegisterViewModelEvent> = _uiEvent

    fun register(
        userName: String,
        email: String,
        password: String,
        phoneNumber: String
    ) {

        _uiState.value = RegisterUiState.Loading
        viewModelScope.launch {
            authRepository.registerAccount(
                username = userName,
                email = email,
                password = password,
                phoneNumber = phoneNumber
            ).fold(
                onSuccess = {
                    _uiEvent.value = RegisterViewModelEvent.RegisterSuccess
                },
                onFailure = {
                    _uiEvent.value = RegisterViewModelEvent.Error(
                        it.message?: "Something went wrong"
                    )
                }
            )
        }
        
    }
}

sealed class RegisterUiState {
    data object Loading : RegisterUiState()
}

sealed class RegisterViewModelEvent {
    data object RegisterSuccess : RegisterViewModelEvent()
    data class Error(val error: String) : RegisterViewModelEvent()
}