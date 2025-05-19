package com.project.suitcase.views.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityMainBinding
import com.project.suitcase.views.ui.activity.item.AddItemActivity
import com.project.suitcase.views.ui.activity.trip.AddTripActivity

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var fAuth: FirebaseAuth? = null

    private val rotateopen: Animation by lazy { AnimationUtils. loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateclose: Animation by lazy { AnimationUtils. loadAnimation(this, R.anim.rotate_close_anim) }

    private val fromBottomLeft: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_left) }
    private val fromBottomRight: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_right) }
    private val toBottomLeft: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_left) }
    private val toBottomRight: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_right) }

    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        fAuth = FirebaseAuth.getInstance()

//        val navController = Navigation.findNavController(this, R.id.host_fragment)
//        NavigationUI.setupWithNavController(binding!!.bottomNavigationView, navController)
//        binding?.bottomNavigationView?.menu?.getItem(2)?.isEnabled = false

        val navController = supportFragmentManager.findFragmentById(R.id.host_fragment)
            ?.findNavController()

        navController?.let {
            NavigationUI.setupWithNavController(binding!!.bottomNavigationView, it)
        }

        binding?.bottomNavigationView?.menu?.getItem(2)?.isEnabled = false

        binding?.fabAdd?.setOnClickListener{
            onAddButtonClicked()
        }

        binding?.fabAddNewTrip?.setOnClickListener {
            onAddButtonClicked()
            startActivity(Intent(this@MainActivity, AddTripActivity::class.java))
        }

        binding?.fabAddNewItem?.setOnClickListener {
            onAddButtonClicked()
            startActivity(Intent(this@MainActivity, AddItemActivity::class.java))
        }

        if (intent?.action == Intent.ACTION_SEND) {
            if (intent.type == "text/plain" || fAuth?.currentUser != null) {
                handleSendText(intent) // Handle text being sent
            } 
        }


    }

    private fun onAddButtonClicked() {
        setVisibilty(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked=!clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding?.fabAddNewItem?.visibility = View.VISIBLE
            binding?.fabAddNewTrip?.visibility = View.VISIBLE
        } else {
            binding?.fabAddNewItem?.visibility = View.INVISIBLE
            binding?.fabAddNewTrip?.visibility = View.INVISIBLE
        }
    }

    private fun setVisibilty(clicked: Boolean) {
        if (!clicked) {
            binding?.fabAddNewItem?.startAnimation(fromBottomLeft)
            binding?.fabAddNewTrip?.startAnimation(fromBottomRight)
            binding?.fabAdd?.startAnimation(rotateopen)
        } else {
            binding?.fabAddNewTrip?.startAnimation(toBottomRight)
            binding?.fabAddNewItem?.startAnimation(toBottomLeft)
            binding?.fabAdd?.startAnimation(rotateclose)
        }
    }

    private fun setClickable(clicked: Boolean){
        if(!clicked){
            binding?.fabAddNewItem?.isClickable=true
            binding?.fabAddNewTrip?.isClickable=true
        }
        else{
            binding?.fabAddNewTrip?.isClickable=false
            binding?.fabAddNewItem?.isClickable=false
        }
    }

    private fun handleSendText(intent: Intent) {

        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        sharedText?.let {
            if (isFromGoogleMaps(it)) {
                Log.e("MainActivity", "Shared text is from Google Maps")
            } else {
                Log.e("MainActivity", "Shared text is from another app")
            }

            val addItemIntent = Intent(this, AddItemActivity::class.java)
            addItemIntent.putExtra("sharedText", it)
            startActivity(addItemIntent)
        }
    }
    private fun isFromGoogleMaps(sharedText: String): Boolean {
        // Check for common Google Maps URL patterns
        return sharedText.contains("https://maps.google.com") || sharedText.contains("https://www.google.com/maps")
    }
}