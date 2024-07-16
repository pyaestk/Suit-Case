package com.project.suitcase.view.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.suitcase.databinding.ActivityLoginBinding
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

        binding?.btnLogin?.setOnClickListener {
            viewModel.login(
                password = binding?.edtPassword?.text.toString().trim(),
                email = binding?.edtEmail?.text.toString().trim()
            )
        }
        binding?.btnCreateAccount?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        viewModel.uiState.observe(this) {state ->
            when(state) {
                LoginUiState.Loading -> Toast.makeText(
                    this, "Loading", Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.uiEvent.observe(this) { event ->
            when(event){
                is LoginViewModelEvent.Error -> Toast.makeText(this, event.error, Toast.LENGTH_LONG).show()
                LoginViewModelEvent.LoginSuccess -> {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}