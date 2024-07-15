package com.project.suitcase.view.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.suitcase.R

class MainActivity : AppCompatActivity() {
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        requestLocationPermissions()
//
//        if (intent?.action == Intent.ACTION_SEND) {
//            if (intent.type == "text/plain") {
//                handleSendText(intent) // Handle text being sent
//            }
//        }
    }

//    private fun requestLocationPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
//    }
//
//    private fun handleSendText(intent: Intent) {
//        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
//            val url = it // The shared URL
//            val text = findViewById<TextView>(R.id.text)
//            text.text = url
//        }
//    }
//
//    // Handling the permission result
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                // Permission granted, proceed with location functionality
//            } else {
//                // Permission denied, handle appropriately
//            }
//        }
//    }
}