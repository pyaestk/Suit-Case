package com.project.suitcase.views.ui.activity.auth

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityWelcomeBinding
import com.project.suitcase.views.viewmodel.WelcomeScreenUiState
import com.project.suitcase.views.viewmodel.WelcomeScreenViewModel
import com.project.suitcase.views.ui.activity.MainActivity
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
        setContentView(binding?.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        window.statusBarColor = ContextCompat.getColor(this, R.color.bluewhite_variant)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.bluewhite_variant)

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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