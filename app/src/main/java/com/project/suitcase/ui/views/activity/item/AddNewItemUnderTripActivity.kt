package com.project.suitcase.ui.views.activity.item

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityAddNewItemUnderTripBinding
import com.project.suitcase.ui.viewmodel.AddItemUiState
import com.project.suitcase.ui.viewmodel.AddItemViewModel
import com.project.suitcase.ui.viewmodel.AddItemViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddNewItemUnderTripActivity : AppCompatActivity() {

    private var binding: ActivityAddNewItemUnderTripBinding? = null
    private val addItemVieModel: AddItemViewModel by viewModel()
    private var imageUri: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var tripId: String? = null
    private var tripName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewItemUnderTripBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        registerActivityForResult()

        tripId = intent.getStringExtra("tripId")
        tripName = intent.getStringExtra("tripName")
        Log.d("TripName", tripName.toString())

        binding?.ivItemImage?.setOnClickListener {
            chooseImage()
        }
        binding?.btnSaveItem?.setOnClickListener {
            val itemName = binding?.edtItemName?.text.toString()
            val itemDescription = binding?.edtItemDescription?.text.toString()
            val itemPrice = binding?.edtPrice?.text.toString()
            val itemLocation = binding?.edtLocation?.text.toString()

            tripId?.let {
                addItemVieModel.addItem(
                    tripId = tripId!!,
                    itemPrice = itemPrice,
                    itemDescription = itemDescription,
                    itemLocation = itemLocation,
                    itemImage = imageUri,
                    itemName = itemName,
                    tripName = tripName!!
                )
            }
        }

        binding?.btnBack?.setOnClickListener {
            finish()
        }

        itemViewModelSetUp()

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
                    binding?.progressBar?.visibility = View.INVISIBLE
                    binding?.layoutContent?.visibility = View.VISIBLE
//                    val resultIntent = Intent()
//                    setResult(RESULT_OK, resultIntent)
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