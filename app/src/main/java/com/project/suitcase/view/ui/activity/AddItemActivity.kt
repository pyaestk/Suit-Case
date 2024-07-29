package com.project.suitcase.view.ui.activity

import android.Manifest
import android.R.layout.simple_list_item_1
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
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
import com.project.suitcase.view.viewmodel.AddItemGetTripsViewModelEvent
import com.project.suitcase.view.viewmodel.AddItemUiState
import com.project.suitcase.view.viewmodel.AddItemViewModel
import com.project.suitcase.view.viewmodel.AddItemViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddItemActivity : AppCompatActivity() {

    private var binding: ActivityAddItemBinding? = null

    private val addItemVieModel: AddItemViewModel by viewModel()


    private lateinit var tripMap: Map<String, String>
    private var imageUri: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var tripId: String? = null
    override fun onResume() {
        super.onResume()
        addItemVieModel.getTrips()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        registerActivityForResult()

        tripId = intent.getStringExtra("tripId")

        intent.getStringExtra("sharedText")?.let { sharedText ->
            processSharedText(sharedText)
        }

        binding?.imageView?.setOnClickListener {
            chooseImage()
        }
        binding?.btnSaveItem?.setOnClickListener {
            val selectedTrip = binding?.edtTrip?.text.toString()
            val tripId = tripMap[selectedTrip]
            val itemName = binding?.edtItemName?.text.toString()
            val itemDescription = binding?.edtItemDescription?.text.toString()
            val itemPrice = binding?.edtPrice?.text.toString()
            val itemLocation = binding?.edtLocation?.text.toString()
            val itemImage = imageUri

            if (tripId != null) {

                addItemVieModel.addItem(
                    tripId = tripId,
                    itemPrice = itemPrice,
                    itemDescription = itemDescription,
                    itemLocation = itemLocation,
                    itemImage = itemImage!!,
                    itemName = itemName,
                )

            }


        }

        binding?.btnAddNewTrip?.setOnClickListener {
            startActivity(Intent(this@AddItemActivity, AddTripActivity::class.java))
        }
        binding?.btnBack?.setOnClickListener {
            finish()
        }

        itemViewModelSetUp()
        tripViewModelSetUp()
    }

    private fun processSharedText(sharedText: String) {
        val linkMatcher = Patterns.WEB_URL.matcher(sharedText)
        var link = ""
        var nonLinkText = sharedText

        while (linkMatcher.find()) {
            link = linkMatcher.group()
            nonLinkText = nonLinkText.replace(link, "").trim()
        }

        if (link.isNotEmpty()) {
            binding?.edtItemDescription?.setText(link)
        }

        if (nonLinkText.isNotEmpty()) {
            if (isFromGoogleMaps(link)) {
                binding?.edtLocation?.setText(link)
            } else {
                binding?.edtItemName?.setText(nonLinkText)
            }
        }
    }

    private fun tripViewModelSetUp() {
        addItemVieModel.tripListUiEvent.observe(this) {event ->
            when(event) {
                is AddItemGetTripsViewModelEvent.Error -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_SHORT).show()
                }
                is AddItemGetTripsViewModelEvent.Success -> {
                    tripMap = event.trips.associateBy({ it.tripName }, { it.tripId })
                    val tripsName = event.trips.map { it.tripName }
                    val adapter = ArrayAdapter(applicationContext, simple_list_item_1, tripsName)
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

        addItemVieModel.addItemUiEvent.observe(this){ event ->
            when(event) {
                is AddItemViewModelEvent.Error -> {
                    Toast.makeText(
                        this, event.error, Toast.LENGTH_SHORT
                    ).show()
                }
                is AddItemViewModelEvent.Success -> {
                    Toast.makeText(
                        this, "Item added Successfully", Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
        addItemVieModel.uiState.observe(this) { state ->
            when(state) {
                AddItemUiState.Loading -> {
                    Toast.makeText(
                        this, "Item Adding..", Toast.LENGTH_SHORT
                    ).show()

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