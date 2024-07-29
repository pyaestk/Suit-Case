package com.project.suitcase.view.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityItemListBinding
import com.project.suitcase.view.adapter.ItemsListAdapter
import com.project.suitcase.view.viewmodel.ItemListDeleteViewModelEvent
import com.project.suitcase.view.viewmodel.ItemListUiState
import com.project.suitcase.view.viewmodel.ItemListViewModel
import com.project.suitcase.view.viewmodel.ItemListViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class ItemListActivity : AppCompatActivity() {

    private var binding: ActivityItemListBinding? = null
    private var tripId: String? = null

    private var itemAdapter: ItemsListAdapter? = null

    private val itemListViewModel: ItemListViewModel by viewModel()

    override fun onResume() {
        super.onResume()
        tripId?.let {
            itemListViewModel.getItemsByTrip(it)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val intent = intent
        tripId = intent.getStringExtra("tripId")
        binding?.tvTripName?.text = intent.getStringExtra("tripName")
        binding?.tvTripDate?.text = intent.getStringExtra("tripDate")

        adapterSetup()

        itemListViewModelSetup()

        binding?.btnBack?.setOnClickListener {
            finish()
        }

        itemAdapter?.onCheckBoxClick = { itemId, tripId, isChecked ->

            itemListViewModel.updateItemStatus(
                finished = isChecked,
                tripId = tripId,
                itemId = itemId
            )

            if(isChecked) {
                itemListViewModel.moveToFinished(tripId = tripId, itemId = itemId)
            } else {
                itemListViewModel.removeFromFinished(itemId = itemId)
            }
        }
        binding?.btnAddItem?.setOnClickListener{
            val intentNavigate = Intent(this@ItemListActivity, AddItemActivity::class.java).apply {
                putExtra("tripId", tripId)
            }
            startActivity(intentNavigate)
        }
        binding?.btnDeleteAll?.setOnClickListener {
            MaterialAlertDialogBuilder(this@ItemListActivity,
                R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle("Do you want to delete all items?")
                .setMessage("All items will be removed permanently from device.")
                .setNegativeButton("NO") { dialog, which ->
                    //nothing
                }
                .setPositiveButton("YES") { dialog, which ->
                    tripId?.let {
                        itemListViewModel.deleteAllItems(it)
                    }
                }
                .show()
        }

    }

    private fun adapterSetup() {
        itemAdapter = ItemsListAdapter()
        binding?.rvItemList?.apply {

//            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//            adapter = itemAdapter

            layoutManager = LinearLayoutManager(this@ItemListActivity,
                LinearLayoutManager.VERTICAL, false)
            adapter = itemAdapter
        }
    }

    private fun itemListViewModelSetup() {
        itemListViewModel.uiState.observe(this) {state ->
            when(state){
                ItemListUiState.Loading -> {

                }
            }
        }
        itemListViewModel.itemListUiEvent.observe(this) {event ->
            when(event) {
                is ItemListViewModelEvent.Error -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_SHORT).show()
                }
                is ItemListViewModelEvent.Success -> {
                    itemAdapter?.setItemList(event.itemList)
                }
            }
        }

        itemListViewModel.itemListDeleteUiEvent.observe(this) { event ->
            when(event) {
                is ItemListDeleteViewModelEvent.Error -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_SHORT).show()
                }
                ItemListDeleteViewModelEvent.Success -> {
                    Toast.makeText(this, "All items has been deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}