package com.project.suitcase.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.data.repository.AuthRepository
import com.project.suitcase.ui.viewmodel.util.SingleLiveEvent
import com.project.suitcase.ui.viewmodel.util.Validator
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val validator: Validator
): ViewModel() {

    private val _loginFormState = MutableLiveData(LoginFormState())
    val loginFormState: LiveData<LoginFormState> = _loginFormState

    private val _uiState = MutableLiveData<LoginUiState>()
    val uiState: LiveData<LoginUiState> = _uiState

    private val _uiEvent = SingleLiveEvent<LoginViewModelEvent>()
    val uiEvent: LiveData<LoginViewModelEvent> = _uiEvent

    fun onEvent(loginFormEvent: LoginFormEvent) {
        when (loginFormEvent) {
            is LoginFormEvent.EmailChanged -> {
                _loginFormState.value = _loginFormState.value?.copy(email = loginFormEvent.email)
            }

            is LoginFormEvent.PasswordChange -> {
                _loginFormState.value =
                    _loginFormState.value?.copy(password = loginFormEvent.password)
            }
            is LoginFormEvent.Submit -> {
                _loginFormState.value?.let {
                    val emailResult = validator.emailValidator(it.email)
                    val passwordResult = it.password.isNotBlank()

                    if (emailResult.successful && passwordResult) {
                        login(it.email, it.password)
                    }else{
                        _loginFormState.value = _loginFormState.value?.copy(
                            emailError = emailResult.errorMessage,
                        )
                    }
                }
            }
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

    fun clearEmailError() {
        _loginFormState.value = _loginFormState.value?.copy(emailError = null)
    }

}

sealed class LoginUiState {
    data object Loading : LoginUiState()
}

sealed class LoginViewModelEvent {
    data object LoginSuccess : LoginViewModelEvent()
    data class Error(val error: String) : LoginViewModelEvent()
}
sealed class LoginFormEvent {
    data class EmailChanged(val email: String) : LoginFormEvent()
    data class PasswordChange(val password: String) : LoginFormEvent()
    data object Submit : LoginFormEvent()
}

data class LoginFormState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
)