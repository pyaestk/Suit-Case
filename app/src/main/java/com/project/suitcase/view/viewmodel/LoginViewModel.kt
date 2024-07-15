package com.project.suitcase.view.viewmodel

import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

}

sealed class LoginUiState {
    data object Loading : LoginUiState()
}

sealed class LoginViewModelEvent {
    data object LoginSuccess : LoginViewModelEvent()
    data class Error(val error: String) : LoginViewModelEvent()
}