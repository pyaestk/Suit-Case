package com.project.suitcase.view.ui.activity.auth

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.project.suitcase.databinding.ActivityLoginBinding
import com.project.suitcase.view.viewmodel.LoginFormEvent
import com.project.suitcase.view.viewmodel.LoginUiState
import com.project.suitcase.view.viewmodel.LoginViewModel
import com.project.suitcase.view.viewmodel.LoginViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null
    private val viewModel: LoginViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        enableEdgeToEdge()

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        binding?.btnLogin?.setOnClickListener {
            viewModel.onEvent(LoginFormEvent.Submit)
        }

        binding?.btnBack?.setOnClickListener {
            finish()
        }

        binding?.btnTvRegister?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        setUpTextChangeEventListener()

        viewModel.loginFormState.observe(this) {
            binding?.textInputLayoutEmail?.error = it.emailError
            if (it.email.isNotEmpty() && it.password.isNotEmpty()) {
                binding?.btnLogin?.isEnabled = true
            } else {
                binding?.btnLogin?.isEnabled = false
            }
        }

        viewModel.uiState.observe(this) {
            when(it) {
                LoginUiState.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                    binding?.btnLogin?.isEnabled = false
                }
            }
        }

        viewModel.uiEvent.observe(this) { event ->
            when(event){
                is LoginViewModelEvent.Error -> {
                    Toast.makeText(this,
                        "Invalid data", Toast.LENGTH_LONG).show()
                    binding?.btnLogin?.isEnabled = true
                }
                LoginViewModelEvent.LoginSuccess -> {
                    binding?.btnLogin?.isEnabled = false
                    finish()
                }
            }
        }
    }

    private fun setUpTextChangeEventListener() {
        binding?.apply {
            edtEmail.addTextChangedListener {
                viewModel.onEvent(LoginFormEvent.EmailChanged(it.toString()))
                viewModel.clearEmailError()
            }
            edtPassword.addTextChangedListener {
                viewModel.onEvent(LoginFormEvent.PasswordChange(it.toString()))
            }
        }
    }
}