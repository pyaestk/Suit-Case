package com.project.suitcase.view.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.project.suitcase.databinding.ActivityWelcomeBinding
import com.project.suitcase.view.ui.activity.auth.LoginActivity
import com.project.suitcase.view.ui.activity.auth.RegisterActivity

class WelcomeActivity : AppCompatActivity() {

    private var binding: ActivityWelcomeBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding?.root)

        binding?.btnLogin?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding?.btnRegister?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }
}