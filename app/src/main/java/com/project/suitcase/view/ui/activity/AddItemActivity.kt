package com.project.suitcase.view.ui.activity

import android.Manifest
import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.project.suitcase.databinding.ActivityAddItemBinding
import com.project.suitcase.view.viewmodel.GetTripViewModelEvent
import com.project.suitcase.view.viewmodel.ImageUIViewModelEvent
import com.project.suitcase.view.viewmodel.ImageUiState
import com.project.suitcase.view.viewmodel.ItemUiState
import com.project.suitcase.view.viewmodel.ItemViewModel
import com.project.suitcase.view.viewmodel.ItemViewModelEvent
import com.project.suitcase.view.viewmodel.TripViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddItemActivity : AppCompatActivity() {

    private var binding: ActivityAddItemBinding? = null
    private val itemViewModel: ItemViewModel by viewModel()
    private val tripViewModel: TripViewModel by viewModel()


    private lateinit var tripMap: Map<String, String>
    private var imageUri: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var tripId: String? = null
    override fun onResume() {
        super.onResume()
        tripViewModel.getTrips()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        registerActivityForResult()

//        intent.getStringExtra("sharedText")?.let { sharedText ->
//            binding?.edtItemName?.setText(sharedText)
//        }
        
        tripId = intent.getStringExtra("tripId")


        intent.getStringExtra("sharedText")?.let { sharedText ->
            if (isFromGoogleMaps(sharedText)) {
                binding?.edtLocation?.setText(sharedText)
            } else {
                binding?.edtItemName?.setText(sharedText)
            }
        }

        binding?.imageView?.setOnClickListener {
            chooseImage()
        }
        binding?.btnSaveItem?.setOnClickListener {
            val selectedTrip = binding?.edtTrip?.text.toString()
            val tripId = tripMap[selectedTrip]

            if (tripId != null && imageUri != null) {
                // Upload image first
                itemViewModel.uploadImage(imageUri!!)
            } else {
                Toast.makeText(this, "Please select a valid trip and an image", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.btnNewTrip?.setOnClickListener {
            startActivity(Intent(this@AddItemActivity, AddTripActivity::class.java))
        }
        binding?.btnBack?.setOnClickListener {
            finish()
        }

        itemViewModelSetUp()
        tripViewModelSetUp()
    }

    private fun tripViewModelSetUp() {
        tripViewModel.tripListUiEvent.observe(this) { event ->
            when(event) {
                is GetTripViewModelEvent.Error -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_SHORT).show()
                }
                is GetTripViewModelEvent.Success -> {
                    tripMap = event.trips.associateBy({ it.tripName }, { it.tripId })
                    val tripsName = event.trips.map { it.tripName }
                    val adapter = ArrayAdapter(applicationContext, R.layout.simple_list_item_1, tripsName)
                    binding?.edtTrip?.setAdapter(adapter)
                    
                    tripId?.let { id ->
                        val tripName = tripMap.entries.find { it.value == id }?.key
                        if (tripName != null) {
                            binding?.edtTrip?.setText(tripName, false) // Set the trip name without triggering dropdown
                        }
                    }
                }
            }
        }
    }

    private fun itemViewModelSetUp() {
        //for item adding
        itemViewModel.uiState.observe(this){ state ->
            when(state) {
                ItemUiState.Loading -> {
                    Toast.makeText(this, "Adding Loading", Toast.LENGTH_SHORT).show()
                }
            }
        }
        itemViewModel.addItemUiEvent.observe(this) { event ->
            when(event) {
                is ItemViewModelEvent.Error -> {
                    Toast.makeText(
                        this, event.error, Toast.LENGTH_SHORT
                    ).show()
                }
                is ItemViewModelEvent.Success -> {
                    Toast.makeText(
                        this, "Success", Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }

        //for item image adding
        itemViewModel.imageUiState.observe(this){
            when(it){
                ImageUiState.Loading -> {

                }
            }
        }
        itemViewModel.imageUiEvent.observe(this) { event ->
            when(event){
                is ImageUIViewModelEvent.Error -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_SHORT)
                        .show()
                }
                is ImageUIViewModelEvent.Success -> {
                    // Image uploaded successfully, now add item
                    val itemName = binding?.edtItemName?.text.toString()
                    val itemDescription = binding?.edtItemDescription?.text.toString()
                    val itemPrice = binding?.edtPrice?.text.toString()
                    val selectedTrip = binding?.edtTrip?.text.toString()
                    val itemLocation = binding?.edtLocation?.text.toString()
                    val tripId = tripMap[selectedTrip]
                    val itemImage = event.imageUri

                    if (tripId != null) {
                        itemViewModel.addItem(
                            itemName = itemName,
                            itemImage = itemImage,
                            itemDescription = itemDescription,
                            itemLocation = itemLocation,
                            itemPrice = itemPrice,
                            tripId = tripId
                        )
                    }
                }
            }
        }

    }

    private fun isFromGoogleMaps(sharedText: String): Boolean {

        return sharedText.contains("https://maps.app.goo.gl") || sharedText.contains("https://www.google.com/maps")
    }

    private fun chooseImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }

    private fun registerActivityForResult() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                val resultCode = result.resultCode
                val imageData = result.data
                if (resultCode == RESULT_OK && imageData != null) {
                    imageUri = imageData.data
                    imageUri?.let {
                        Glide.with(applicationContext)
                            .load(it)
                            .into(binding?.imageView!!)
                    }
                }
            }
        )
    }

}