package com.project.suitcase.views.ui.activity.auth

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.project.suitcase.databinding.ActivityRegisterBinding
import com.project.suitcase.views.viewmodel.RegisterUiState
import com.project.suitcase.views.viewmodel.RegisterViewModel
import com.project.suitcase.views.viewmodel.RegisterViewModelEvent
import com.project.suitcase.views.viewmodel.RegistrationFormEvent
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null
    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        ViewCompat.setOnApplyWindowInsetsListener(binding!!.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding?.btnRegister?.isEnabled = false

        binding?.btnRegister?.setOnClickListener{
            viewModel.onEvent(RegistrationFormEvent.Submit)
        }

        binding?.btnBack?.setOnClickListener {
            finish()
        }
        
        observeViewModel()
        setUpTextChangeListenerEvent()

    }

    private fun observeViewModel() {
        //for form validation
        viewModel.formState.observe(this) {
            binding?.apply {
                registerBtnVisibility(it.name.isNotEmpty() and it.email.isNotEmpty())
                textInputLayoutEmail.error = it.emailError
                textInputLayoutPassword.error = it.passwordError
                textInputLayoutPasswordRepeat.error = it.repeatedPasswordError
            }
        }
        //for registration
        viewModel.uiState.observe(this){ state ->
            when(state){
                RegisterUiState.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                    binding?.btnRegister?.isEnabled = false
                }
            }
        }

        viewModel.uiEvent.observe(this) { event ->
            when(event){
                is RegisterViewModelEvent.Error -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_LONG).show()
                    binding?.btnRegister?.isEnabled = true
                }
                RegisterViewModelEvent.RegisterSuccess -> {
                    binding?.btnRegister?.isEnabled = false
                    finish()
                }
            }
        }
    }

    private fun setUpTextChangeListenerEvent() {
        binding?.apply {
            edtUserName.addTextChangedListener {
                viewModel.onEvent(RegistrationFormEvent.NameChanged(it.toString()))
            }
            edtEmail.addTextChangedListener {
                viewModel.onEvent(RegistrationFormEvent.EmailChanged(it.toString()))
                viewModel.clearEmailError()
                textInputLayoutEmail.isErrorEnabled = false
            }
            edtPassword.addTextChangedListener {
                viewModel.onEvent(RegistrationFormEvent.PasswordChanged(it.toString()))
                viewModel.clearPasswordError()
                textInputLayoutPassword.isErrorEnabled = false
            }
            edtPasswordRepeat.addTextChangedListener {
                viewModel.onEvent(RegistrationFormEvent.RepeatedPasswordChanged(it.toString()))
                viewModel.clearRepeatedPasswordError()
                textInputLayoutPasswordRepeat.isErrorEnabled = false
            }
            edtPhoneNum.addTextChangedListener{
                viewModel.onEvent(RegistrationFormEvent.PhoneNumberChanged(it.toString()))
            }
        }
    }

    private fun registerBtnVisibility(shouldShow: Boolean) {
        if (shouldShow) {
            binding?.btnRegister?.isEnabled = true
        } else {
            binding?.btnRegister?.isEnabled = false
        }
    }

}