package com.project.suitcase.views.ui.activity.auth

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityLoginBinding
import com.project.suitcase.views.viewmodel.LoginFormEvent
import com.project.suitcase.views.viewmodel.LoginUiState
import com.project.suitcase.views.viewmodel.LoginViewModel
import com.project.suitcase.views.viewmodel.LoginViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null
    private val viewModel: LoginViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        window.statusBarColor = ContextCompat.getColor(this, R.color.bluewhite_variant)

        binding?.btnLogin?.setOnClickListener {
            viewModel.onEvent(LoginFormEvent.Submit)
        }

        binding?.btnBack?.setOnClickListener {
            finish()
        }

        binding?.btnTvRegister?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        observeViewModel()
        setUpTextChangeEventListener()
    }

    private fun observeViewModel() {
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