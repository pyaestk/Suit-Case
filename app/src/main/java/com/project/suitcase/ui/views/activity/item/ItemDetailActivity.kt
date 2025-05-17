package com.project.suitcase.ui.views.activity.item

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityItemDetailBinding
import com.project.suitcase.domain.model.ItemDetailModel
import com.project.suitcase.ui.viewmodel.ItemDetailUiState
import com.project.suitcase.ui.viewmodel.ItemDetailViewModel
import com.project.suitcase.ui.viewmodel.ItemDetailViewModelEvent
import com.project.suitcase.ui.viewmodel.ItemListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ItemDetailActivity : AppCompatActivity() {

    private var binding: ActivityItemDetailBinding? = null
    private var tripId: String? = null
    private var itemId: String? = null
    private var itemImage: String? = null

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        binding?.btnBack?.setOnClickListener {
            finish()
        }

        val intent = intent
        tripId = intent.getStringExtra("tripID")
        itemId = intent.getStringExtra("itemID")

        itemDetailViewModel()

        binding?.btnShare?.setOnClickListener {
            shareItemDetails()
        }
        binding?.switchFinish?.setOnCheckedChangeListener { _, isChecked ->
            itemListViewModel.updateItemCheckedStatus(
                itemId = itemId!!, finished = isChecked, tripId = tripId!!)
            val resultIntent = Intent()
            setResult(RESULT_OK, resultIntent)

        }
        binding?.btnItemEdit?.setOnClickListener {
            val intentNavigate = Intent(this@ItemDetailActivity,
                ItemEditActivity::class.java).apply { putExtra("tripID", tripId)
                putExtra("itemID", itemId)
                putExtra("itemImage", itemImage)
            }
            startActivity(intentNavigate)
        }

    }

    private fun shareItemDetails() {

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

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
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
    }
    private fun updateUI(itemDetailModel: ItemDetailModel) {
        binding?.layoutContent?.visibility = View.VISIBLE
        binding?.progressBar?.visibility = View.INVISIBLE

        binding?.edtItemName?.setText(itemDetailModel.itemName)
        binding?.edtPrice?.setText(itemDetailModel.itemPrice)
        binding?.edtLocation?.setText(itemDetailModel.itemLocation)
        binding?.edtItemDescription?.setText(itemDetailModel.itemDescription)
        itemImage = itemDetailModel.itemImage

        if (itemDetailModel.finished == true) {
            binding?.switchFinish?.isChecked = true
        } else {
            binding?.switchFinish?.isChecked = false
        }
        if (itemImage.isNullOrEmpty()) {
            binding?.ivItemImage?.setImageResource(R.drawable.photo)
        } else {
            Glide.with(this)
                .load(itemImage)
                .into(binding?.ivItemImage!!)
            binding?.ivItemImage?.setBackgroundResource(R.color.white)
        }
    }

}