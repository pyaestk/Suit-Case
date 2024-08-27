package com.project.suitcase.view.ui.activity.auth

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.project.suitcase.databinding.ActivityWelcomeBinding
import com.project.suitcase.view.ui.activity.MainActivity
import com.project.suitcase.view.viewmodel.WelcomeScreenUiState
import com.project.suitcase.view.viewmodel.WelcomeScreenViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WelcomeActivity : AppCompatActivity() {

    private var binding: ActivityWelcomeBinding? = null
    private val viewModel: WelcomeScreenViewModel by viewModel()

    override fun onResume() {
        super.onResume()
        viewModel.checkUserRegistration()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding?.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        binding?.btnLogin?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding?.btnRegister?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.uiState.observe(this) {state ->
            when(state) {
                WelcomeScreenUiState.Loading -> Toast.makeText(
                    this, "Loading", Toast.LENGTH_SHORT
                ).show()

                WelcomeScreenUiState.NavigateToMainScreen -> {
                    startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                    finish()
                }
            }
        }

    }
}