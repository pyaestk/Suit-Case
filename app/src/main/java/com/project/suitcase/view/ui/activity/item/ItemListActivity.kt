package com.project.suitcase.view.ui.activity.item

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityItemListBinding
import com.project.suitcase.view.adapter.ItemListDiffUtilAdapter
import com.project.suitcase.view.viewmodel.ItemListUiState
import com.project.suitcase.view.viewmodel.ItemListViewModel
import com.project.suitcase.view.viewmodel.ItemListViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class ItemListActivity : AppCompatActivity(), SensorEventListener{

    private var binding: ActivityItemListBinding? = null
    private var tripId: String? = null
    private var tripName: String? = null

    private var itemAdapter: ItemListDiffUtilAdapter? = null

    private val itemListViewModel: ItemListViewModel by viewModel()

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    private var lastZ: Float = 0.0f

    private var shakeThreshold: Float = 2.0f
    // Add a flag to prevent multiple dialogs
    private var isDialogShowing = false
    private var isInitialized = false

    override fun onResume() {
        super.onResume()
        tripId?.let {
            itemListViewModel.getItemsByTrip(it)
        }
        accelerometer?.let { acc ->
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val intent = intent
        tripId = intent.getStringExtra("tripId")
        tripName = intent.getStringExtra("tripName")
        binding?.tvTripName?.text = tripName
        binding?.tvTripDate?.text = intent.getStringExtra("tripDate")

        adapterSetup()
        itemListViewModelSetup()
        onClickEvent()

        gestureSwipeEvent()
        sensorShakeEvent()
    }


    private fun sensorShakeEvent() {
        // Getting the Sensor Manager instance
        sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun gestureSwipeEvent() {
        val itemTouchHelperRight = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val currentItem = itemAdapter?.differ?.currentList?.get(position)
                itemListViewModel.deleteItem(
                    tripId = currentItem!!.tripId,
                    itemId = currentItem.itemId
                )
            }

        }
        val touchHelperRight = ItemTouchHelper(itemTouchHelperRight)
        touchHelperRight.attachToRecyclerView(binding?.rvItemList)

        val itemTouchHelperLeft = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val currentItem = itemAdapter?.differ?.currentList?.get(position)
                val message = """
                    Check out this item!
                    Name: ${currentItem?.itemName}
                    Price: ${currentItem?.itemPrice}
                    Location: ${currentItem?.itemLocation}
                    Description: ${currentItem?.itemDescription}
                """.trimIndent()

                val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("sms:")
                    putExtra("sms_body", message)
                }

                val shareIntent = Intent.createChooser(smsIntent, null)
                startActivity(shareIntent)

                itemAdapter?.notifyItemChanged(position)
            }

        }
        val touchHelperLeft = ItemTouchHelper(itemTouchHelperLeft)
        touchHelperLeft.attachToRecyclerView(binding?.rvItemList)
    }


    private fun onClickEvent() {
        binding?.btnBack?.setOnClickListener {
            finish()
        }

        itemAdapter?.onCheckBoxClick = { itemId, tripId, isChecked ->
            itemListViewModel.updateItemCheckedStatus(
                finished = isChecked,
                tripId = tripId,
                itemId = itemId
            )
        }
        binding?.btnAddItem?.setOnClickListener{
            val intentNavigate = Intent(this@ItemListActivity,
                AddNewItemUnderTripActivity::class.java).apply {
                putExtra("tripId", tripId)
                putExtra("tripName", tripName)
            }
            startActivity(intentNavigate)
        }
        binding?.btnDeleteAll?.setOnClickListener {
            MaterialAlertDialogBuilder(this@ItemListActivity,
                R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle("Delete all items?")
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

        itemAdapter?.onItemClick = {
            val intent = Intent(this@ItemListActivity, ItemDetailActivity::class.java).apply {
                putExtra("itemID", it.itemId)
                putExtra("tripID", it.tripId)
                putExtra("itemImage", it.itemImage)
                putExtra("tripName", it.tripName)
            }
            startActivity(intent)
        }
        //Item left and right swipe

    }

    private fun adapterSetup() {
        itemAdapter = ItemListDiffUtilAdapter()
        binding?.rvItemList?.apply {
            layoutManager = LinearLayoutManager(this@ItemListActivity,
                LinearLayoutManager.VERTICAL, false)
            adapter = itemAdapter
        }
    }

    private fun itemListViewModelSetup() {
        itemListViewModel.uiState.observe(this) {state ->
            when(state){
                is ItemListUiState.Loading -> {

                }
                is ItemListUiState.Success -> {
                    itemAdapter?.differ?.submitList(state.itemList)
                    if (state.itemList.size < 1) {
                        binding?.emptyLayout?.visibility = View.VISIBLE
                    } else {
                        binding?.emptyLayout?.visibility = View.INVISIBLE
                    }
                }
                ItemListUiState.DeleteAllSuccess -> {
                    Toast.makeText(this, "All items has been deleted", Toast.LENGTH_SHORT).show()
                }
                ItemListUiState.DeleteSuccess -> {
                    Toast.makeText(this, "Items has been deleted", Toast.LENGTH_SHORT).show()
                }
                ItemListUiState.MarkAllFinishedSuccess -> {
                    Toast.makeText(this@ItemListActivity,"All item has been marked as finished",
                        Toast.LENGTH_SHORT).show()
                    tripId?.let {
                        itemListViewModel.getItemsByTrip(it)
                    }
                }

                ItemListUiState.UpdateSUccess -> {

                }
            }
        }
        itemListViewModel.itemListUiEvent.observe(this) {event ->
            when(event) {
                is ItemListViewModelEvent.Error -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            if (!isInitialized) {
                // Initialize the sensor values
                lastX = x
                lastY = y
                lastZ = z
                isInitialized = true
                return
            }

            val deltaX = x - lastX
            val deltaY = y - lastY
            val deltaZ = z - lastZ

            lastX = x
            lastY = y
            lastZ = z

            val shake = Math.sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble())
            if (shake > shakeThreshold && !isDialogShowing) {
                Log.d("ShakeDetector", "Shake detected")
                isDialogShowing = true
                MaterialAlertDialogBuilder(this@ItemListActivity,
                    R.style.ThemeOverlay_App_MaterialAlertDialog)
                    .setTitle("Mark all items as finished?")
                    .setMessage("Do you want to mark all items as finished")
                    .setNegativeButton("NO") { dialog, which ->
                        isDialogShowing = false
                    }
                    .setPositiveButton("YES") { dialog, which ->
                        tripId?.let {
                            itemListViewModel.markAllItemsAsFinished(it)
                        }
                        isDialogShowing = false
                    }
                    .show()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        
    }
}

