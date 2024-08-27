package com.project.suitcase.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.project.suitcase.data.repository.AuthRepository
import com.project.suitcase.view.utils.SingleLiveEvent

class WelcomeScreenViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _uiState = MutableLiveData<WelcomeScreenUiState>()
    val uiState: LiveData<WelcomeScreenUiState> = _uiState

    private val _uiEvent = SingleLiveEvent<WelcomeScreenUIEvent>()
    val uiEvent: LiveData<WelcomeScreenUIEvent> = _uiEvent

    fun checkUserRegistration(){
        val fAuth = FirebaseAuth.getInstance()
        val user = fAuth.currentUser
        if (user != null){
            _uiState.value = WelcomeScreenUiState.NavigateToMainScreen
        }
    }
}

sealed class WelcomeScreenUiState {
    data object Loading : WelcomeScreenUiState()
    data object NavigateToMainScreen: WelcomeScreenUiState()

}
sealed class WelcomeScreenUIEvent() {
    data class Error(var e: String): WelcomeScreenUIEvent()
}