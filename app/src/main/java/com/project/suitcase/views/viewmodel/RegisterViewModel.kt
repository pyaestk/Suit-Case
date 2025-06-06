package com.project.suitcase.views.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.suitcase.domain.repository.AuthRepository
import com.project.suitcase.views.viewmodel.util.SingleLiveEvent
import com.project.suitcase.views.viewmodel.util.ValidationResult
import com.project.suitcase.views.viewmodel.util.Validator
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val validator: Validator
): ViewModel() {

    private val _uiState = MutableLiveData<RegisterUiState>()
    val uiState: LiveData<RegisterUiState> = _uiState

    private val _uiEvent = SingleLiveEvent<RegisterViewModelEvent>()
    val uiEvent: LiveData<RegisterViewModelEvent> = _uiEvent

    private val _formState = MutableLiveData(RegistrationFormState())
    val formState: LiveData<RegistrationFormState> = _formState

    fun onEvent(event: RegistrationFormEvent) {
        when (event) {
            is RegistrationFormEvent.NameChanged -> {
                _formState.value = _formState.value?.copy(name = event.displayName)
            }

            is RegistrationFormEvent.EmailChanged -> {
                _formState.value = _formState.value?.copy(email = event.email)
            }

            is RegistrationFormEvent.PasswordChanged -> {
                _formState.value = _formState.value?.copy(password = event.password)
            }

            is RegistrationFormEvent.RepeatedPasswordChanged -> {
                _formState.value = _formState.value?.copy(repeatedPassword = event.repeatedPassword)
            }

            is RegistrationFormEvent.PhoneNumberChanged -> {
                _formState.value = _formState.value?.copy(phone = event.phoneNumber)
            }
            
            is RegistrationFormEvent.Submit -> {
                submitData()
            }

        }
    }

    fun clearEmailError() {
        _formState.value = _formState.value?.copy(emailError = null)
    }

    fun clearPasswordError() {
        _formState.value = _formState.value?.copy(passwordError = null)
    }

    fun clearRepeatedPasswordError() {
        _formState.value = _formState.value?.copy(repeatedPasswordError = null)
    }

    private fun submitData() {
        val currentState = _formState.value ?: return

        val nameResult = if (currentState.name.isBlank()) {
            ValidationResult(successful = false, errorMessage = "Your name can't be empty")
        } else {
            ValidationResult(successful = true)
        }
        val emailResult = validator.emailValidator(currentState.email)
        val passwordResult = validator.passwordValidator(currentState.password)
        val repeatedPasswordResult = validator.confirmPasswordValidator(
            currentState.password, currentState.repeatedPassword
        )

        val hasError = listOf(
            emailResult,
            passwordResult,
            repeatedPasswordResult,
            nameResult
        ).any { !it.successful }

        if (hasError) {
            _formState.value = currentState.copy(
                nameError = nameResult.errorMessage,
                emailError = emailResult.errorMessage,
                passwordError = passwordResult.errorMessage,
                repeatedPasswordError = repeatedPasswordResult.errorMessage,
            )
            return
        } else {
            register(
                userName = currentState.name,
                email = currentState.email,
                password = currentState.password,
                phoneNumber = currentState.phone
            )
        }
    }

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

sealed class RegistrationFormEvent {
    data class NameChanged(val displayName: String) : RegistrationFormEvent()
    data class EmailChanged(val email: String) : RegistrationFormEvent()
    data class PasswordChanged(val password: String) : RegistrationFormEvent()
    data class RepeatedPasswordChanged(
        val repeatedPassword: String
    ) : RegistrationFormEvent()
    data class PhoneNumberChanged(val phoneNumber: String) : RegistrationFormEvent()
    data object Submit : RegistrationFormEvent()
}

data class RegistrationFormState(
    val name: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val repeatedPassword: String = "",
    val repeatedPasswordError: String? = null,
    val phone: String = ""
)