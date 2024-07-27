package com.project.suitcase.view.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.suitcase.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private var binding: ActivityWelcomeBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

    }
}