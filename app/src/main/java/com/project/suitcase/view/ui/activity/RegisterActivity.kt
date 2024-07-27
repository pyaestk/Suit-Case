package com.project.suitcase.view.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.suitcase.databinding.ActivityRegisterBinding
import com.project.suitcase.view.viewmodel.RegisterUiState
import com.project.suitcase.view.viewmodel.RegisterViewModel
import com.project.suitcase.view.viewmodel.RegisterViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null
    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnRegister?.setOnClickListener{
            viewModel.register(
                userName = binding?.edtUserName?.text.toString(),
                password = binding?.edtPassword?.text.toString().trim(),
                phoneNumber = binding?.edtPhoneNum?.text.toString().trim(),
                email = binding?.edtEmail?.text.toString().trim()
            )
        }

        viewModel.uiState.observe(this){ state ->
            when(state){
                RegisterUiState.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.uiEvent.observe(this) { event ->
            when(event){
                is RegisterViewModelEvent.Error -> Toast.makeText(this, event.error, Toast.LENGTH_LONG).show()
                RegisterViewModelEvent.RegisterSuccess -> {
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                }
            }
        }



    }
}