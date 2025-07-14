package com.project.suitcase.views.ui.activity.item

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
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
import com.project.suitcase.databinding.ActivityItemEditBinding
import com.project.suitcase.domain.model.ItemDetailModel

import com.project.suitcase.views.viewmodel.ItemDetailUiState
import com.project.suitcase.views.viewmodel.ItemDetailViewModel
import com.project.suitcase.views.viewmodel.ItemDetailViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class ItemEditActivity : AppCompatActivity() {

    private val detailViewModel: ItemDetailViewModel by viewModel()

    private var tripId: String? = null
    private var itemId: String? = null
    private var imageUri: Uri? = null
    private var itemImage: String? = null

    private var _binding: ActivityItemEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    override fun onResume() {
        super.onResume()
        detailViewModel.getItemDetails(
            itemId = itemId!!,
            tripId = tripId!!
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityItemEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        registerActivityForResult()

        binding.btnBack.setOnClickListener {
            finish()
        }

        val intent = intent
        tripId = intent.getStringExtra("tripID")
        itemId = intent.getStringExtra("itemID")
        itemImage = intent.getStringExtra("itemImage")

        if (itemImage.isNullOrEmpty()) {
            binding.ivItemImage.setImageResource(R.drawable.photo)
        } else {
            Glide.with(this)
                .load(itemImage)
                .into(binding.ivItemImage)
            binding.ivItemImage.setBackgroundResource(R.color.white)
        }

        binding.btnUpdateItem.setOnClickListener{

            if (binding.edtItemName.text.toString().isBlank()){
                binding.textInputLayoutItemName.error = "Item name cannot be blank"
            } else {
                detailViewModel.editItemDetail(
                    tripId = tripId!!,
                    itemId = itemId!!,
                    itemName = binding.edtItemName.text.toString(),
                    itemDescription = binding.edtItemDescription.text.toString(),
                    itemLocation = binding.edtLocation.text.toString(),
                    itemImage = imageUri,
                    itemPrice = binding.edtPrice.text.toString(),
                )
            }

        }

        binding.fabItemImage.setOnClickListener {
            chooseImage()
        }

        itemDetailViewModel()
    }

    private fun itemDetailViewModel() {
        detailViewModel.uiState.observe(this) { state ->
            when (state) {
                ItemDetailUiState.Loading -> {
                    binding.layoutContent.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.VISIBLE
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

                ItemDetailViewModelEvent.EditSuccess -> {
                    finish()
                }

                ItemDetailViewModelEvent.DeleteSuccess -> {
                    finish()
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
                            .into(binding.ivItemImage)
                        binding.ivItemImage.setBackgroundResource(R.color.white)
                    }
                }
            }
        )
    }
    private fun updateUI(itemDetailModel: ItemDetailModel) {
        binding.layoutContent.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE

        binding.edtItemName.setText(itemDetailModel.itemName)
        binding.edtPrice.setText(itemDetailModel.itemPrice)
        binding.edtLocation.setText(itemDetailModel.itemLocation)
        binding.edtItemDescription.setText(itemDetailModel.itemDescription)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}