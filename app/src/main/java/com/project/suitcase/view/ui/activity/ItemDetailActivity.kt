package com.project.suitcase.view.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.project.suitcase.databinding.ActivityItemDetailBinding
import com.project.suitcase.domain.model.ItemDetailModel
import com.project.suitcase.view.viewmodel.EditItemDetailViewModelEvent
import com.project.suitcase.view.viewmodel.ItemDetailUiState
import com.project.suitcase.view.viewmodel.ItemDetailViewModel
import com.project.suitcase.view.viewmodel.ItemDetailViewModelEvent
import com.project.suitcase.view.viewmodel.ItemListViewModel
import com.project.suitcase.view.viewmodel.UpdateItemCheckedStatusViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class ItemDetailActivity : AppCompatActivity() {

    private var binding: ActivityItemDetailBinding? = null
    private var tripId: String? = null
    private var itemId: String? = null
    private var imageUri: Uri? = null
    private var itemImage: String? = null

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val detailViewModel: ItemDetailViewModel by viewModel()
    private val itemListViewModel: ItemListViewModel by viewModel()

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun fetchData() {
        detailViewModel.getItemDetails(
            itemId = itemId!!,
            tripId = tripId!!
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        registerActivityForResult()

        binding?.btnBack?.setOnClickListener {
            finish()
        }

        val intent = intent
        tripId = intent.getStringExtra("tripID")
        itemId = intent.getStringExtra("itemID")
        itemImage = intent.getStringExtra("itemImage")
        if (itemImage.isNullOrEmpty()) {
            binding?.ivItemImage?.setImageResource(R.drawable.photo)
        } else {
            Glide.with(this)
                .load(itemImage)
                .into(binding?.ivItemImage!!)
            binding?.ivItemImage?.setBackgroundResource(R.color.white)
        }


        itemDetailViewModel()

        binding?.btnShare?.setOnClickListener {
            shareItemDetails()
        }
        binding?.ivItemImage?.setOnClickListener {
            chooseImage()
        }
        binding?.switchFinish?.setOnCheckedChangeListener { _, isChecked ->
            itemListViewModel.updateItemCheckedStatus(
                itemId = itemId!!, finished = isChecked, tripId = tripId!!)
        }
        
        binding?.btnUpdateItem?.setOnClickListener{
            detailViewModel.editItemDetail(
                tripId = tripId!!,
                itemId = itemId!!,
                itemName = binding?.edtItemName?.text.toString(),
                itemDescription = binding?.edtItemDescription?.text.toString(),
                itemLocation = binding?.edtLocation?.text.toString(),
                itemImage = imageUri,
                itemPrice = binding?.edtPrice?.text.toString(),
                finished = binding?.switchFinish?.isChecked
            )
        }

    }

    private fun shareItemDetails() {
        // Get the item details from the view model or UI
        val itemName = binding?.edtItemName?.text.toString()
        val itemPrice = binding?.edtPrice?.text.toString()
        val itemLocation = binding?.edtLocation?.text.toString()
        val itemDescription = binding?.edtItemDescription?.text.toString()
        // Prepare the message content
        val message = """
            Check out this item!
            Name: $itemName
            Price: $itemPrice
            Location: $itemLocation
            Description: $itemDescription
        """.trimIndent()

        // Create an SMS Intent
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:")  // This ensures only SMS apps respond
            putExtra("sms_body", message)
        }

        // Check if there is an activity that can handle this intent
        val resolveInfoList = packageManager.queryIntentActivities(smsIntent, 0)
        if (resolveInfoList.isNotEmpty()) {
            startActivity(smsIntent)
        } else {
            Toast.makeText(this, "No SMS app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun itemDetailViewModel() {
        detailViewModel.uiState.observe(this) { state ->
            when (state) {
                ItemDetailUiState.Loading -> {
                    binding?.layoutContent?.visibility = View.INVISIBLE
                    binding?.progressBar?.visibility = View.VISIBLE
                }
            }
        }

        detailViewModel.itemDetailUiEvent.observe(this) {
            when (it) {
                is ItemDetailViewModelEvent.Error -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
                is ItemDetailViewModelEvent.Success -> {
                    updateUI(it.itemDetailModel)
                }
            }
        }

        detailViewModel.editItemDetailUiEvent.observe(this) {
            when (it) {
                is EditItemDetailViewModelEvent.Error -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
                EditItemDetailViewModelEvent.Success -> {
                    Toast.makeText(this, "Item has been updated", Toast.LENGTH_SHORT).show()
                    // Fetch updated data
                    fetchData()
                }
            }
        }
        itemListViewModel.updateItemCheckedStatusUiEvent.observe(this) { event ->
            when(event){
                is UpdateItemCheckedStatusViewModelEvent.Error -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_SHORT).show()
                }
                UpdateItemCheckedStatusViewModelEvent.Success -> {
//                    Toast.makeText(this, "Item has been updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun updateUI(itemDetailModel: ItemDetailModel) {
        binding?.layoutContent?.visibility = View.VISIBLE
        binding?.progressBar?.visibility = View.INVISIBLE

        binding?.edtItemName?.setText(itemDetailModel.itemName)
        binding?.edtPrice?.setText(itemDetailModel.itemPrice)
        binding?.edtLocation?.setText(itemDetailModel.itemLocation)
        binding?.edtItemDescription?.setText(itemDetailModel.itemDescription)
        if (itemDetailModel.finished == true) {
            binding?.switchFinish?.isChecked = true
        } else {
            binding?.switchFinish?.isChecked = false
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
}