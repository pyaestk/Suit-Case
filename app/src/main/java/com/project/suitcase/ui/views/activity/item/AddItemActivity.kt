package com.project.suitcase.ui.views.activity.item

import android.Manifest
import android.R.layout.simple_list_item_1
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityAddItemBinding
import com.project.suitcase.ui.viewmodel.AddItemGetTripsViewModelEvent
import com.project.suitcase.ui.viewmodel.AddItemUiState
import com.project.suitcase.ui.viewmodel.AddItemViewModel
import com.project.suitcase.ui.viewmodel.AddItemViewModelEvent
import com.project.suitcase.ui.views.activity.trip.AddTripActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddItemActivity : AppCompatActivity() {

    private var binding: ActivityAddItemBinding? = null

    private val addItemVieModel: AddItemViewModel by viewModel()


    private lateinit var tripMap: Map<String, String>
    private var imageUri: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onResume() {
        super.onResume()
        addItemVieModel.getTrips()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        registerActivityForResult()

        intent.getStringExtra("sharedText")?.let { sharedText ->
            processSharedText(sharedText)
        }

        binding?.ivItemImage?.setOnClickListener {
            chooseImage()
        }
        binding?.btnSaveItem?.setOnClickListener {
            val selectedTrip = binding?.edtTrip?.text.toString()
            val tripId = tripMap[selectedTrip]
            val itemName = binding?.edtItemName?.text.toString().trim()
            val itemDescription = binding?.edtItemDescription?.text.toString()
            val itemPrice = binding?.edtPrice?.text.toString().trim()
            val itemLocation = binding?.edtLocation?.text.toString()

            if (tripId == null || selectedTrip.isEmpty()) {
                Toast.makeText(this, "Please select a trip.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (itemName.isEmpty()) {
                Toast.makeText(this, "Please enter an item name before saving", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addItemVieModel.addItem(
                tripId = tripId,
                tripName = selectedTrip,
                itemPrice = itemPrice,
                itemDescription = itemDescription,
                itemLocation = itemLocation,
                itemImage = imageUri,
                itemName = itemName,
            )
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
                    binding?.progressBar?.visibility = View.INVISIBLE
                    binding?.layoutContent?.visibility = View.VISIBLE
                    finish()
                }
            }
        }

        addItemVieModel.uiState.observe(this) { state ->
            when(state) {
                AddItemUiState.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                    binding?.layoutContent?.visibility = View.INVISIBLE
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
                            .into(binding?.ivItemImage!!)
                        binding?.ivItemImage?.setBackgroundResource(R.color.white)
                    }
                }
            }
        )
    }
}