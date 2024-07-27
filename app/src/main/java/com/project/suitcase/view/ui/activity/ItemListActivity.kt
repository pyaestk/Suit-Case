package com.project.suitcase.view.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.suitcase.databinding.ActivityItemListBinding
import com.project.suitcase.view.adapter.ItemsListAdapter
import com.project.suitcase.view.viewmodel.ItemListViewModelEvent
import com.project.suitcase.view.viewmodel.ItemUiState
import com.project.suitcase.view.viewmodel.ItemViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ItemListActivity : AppCompatActivity() {

    private var binding: ActivityItemListBinding? = null
    private var tripId: String? = null

    private var itemAdapter: ItemsListAdapter? = null

    private val itemListViewModel: ItemViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val intent = intent
        tripId = intent.getStringExtra("tripId")
        binding?.tvTripName?.text = intent.getStringExtra("tripName")
        binding?.tvTripDate?.text = intent.getStringExtra("tripDate")

        itemAdapter = ItemsListAdapter()
        binding?.rvItemList?.apply {

//            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//            adapter = itemAdapter

            layoutManager = LinearLayoutManager(this@ItemListActivity,
                LinearLayoutManager.VERTICAL, false)
            adapter = itemAdapter
        }

        itemAdapter?.onCheckBoxClick = { itemId, tripId, isChecked ->
            itemListViewModel.updateItemStatus(
                finished = isChecked,
                tripId = tripId,
                itemId = itemId
            )
        }
        binding?.btnAddItem?.setOnClickListener{
            val intentNavigate = Intent(this@ItemListActivity, AddItemActivity::class.java).apply {
                putExtra("tripId", tripId)
            }
            startActivity(intentNavigate)
        }

        tripId?.let { itemListViewModel.getItems(it) }

        tripListViewModelSetup()

        binding?.btnBack?.setOnClickListener {
            finish()
        }

    }

    private fun tripListViewModelSetup() {
        itemListViewModel.uiState.observe(this) {state ->
            when(state){
                ItemUiState.Loading -> {
                    Toast.makeText(this, "Loading..", Toast.LENGTH_SHORT).show()
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
    }
}