package com.project.suitcase.views.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.suitcase.R
import com.project.suitcase.databinding.FragmentHomeBinding
import com.project.suitcase.domain.model.TripDetailModel
import com.project.suitcase.views.adapter.ParentTripAdapter
import com.project.suitcase.views.ui.activity.item.ItemDetailActivity
import com.project.suitcase.views.ui.activity.item.ItemListActivity
import com.project.suitcase.views.viewmodel.HomeFragmentUiState
import com.project.suitcase.views.viewmodel.HomeFragmentViewModel
import com.project.suitcase.views.viewmodel.HomeFragmentViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale


class HomeFragment : Fragment(), ParentTripAdapter.OnTripMenuClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    var parentTripAdapter: ParentTripAdapter? = null

    private val homeFragmentViewModel: HomeFragmentViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        homeFragmentViewModel.getTripsAndItems()
    }

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        parentTripAdapter = ParentTripAdapter().apply {
            setOnTripMenuClickListener(this@HomeFragment)
        }

        binding.rvTrip.apply {
            layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false)
            adapter = parentTripAdapter
        }

        binding.btnDeleteAllTrips?.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle("Delete all Trip?")
                .setMessage("All trips and their items will be removed from the list.")
                .setNegativeButton("NO") { dialog, which ->
                    //nothing
                }
                .setPositiveButton("YES") { dialog, which ->
                    homeFragmentViewModel.deleteAllTrip()
                }
                .show()
        }


        parentTripAdapter?.onItemClick = {
            val intent = Intent(requireContext(), ItemListActivity::class.java).apply {
                putExtra("tripId", it.tripId)
                putExtra("tripName", it.tripName)
                putExtra("tripDate", it.date)
            }
            startActivity(intent)
        }
        parentTripAdapter?.onChildItemClick = {
            val intent = Intent(requireContext(), ItemDetailActivity::class.java).apply {
                putExtra("itemID", it.itemId)
                putExtra("tripID", it.tripId)
                putExtra("itemImage", it.itemImage)
            }
            startActivity(intent)
        }


        viewModelSetUp()

    }

//    private fun appBarSetUp() {
//        binding.rvTrip.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (recyclerView.canScrollVertically(-1)){
//                    binding.appBarTitle.text = "Destinations"
//                } else {
//                    binding.appBarTitle.text = "HOME"
//                }
//            }
//        })
//    }

    private fun viewModelSetUp() {
        homeFragmentViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when(state) {
                HomeFragmentUiState.Loading -> {
                    binding.rvTrip.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.VISIBLE
                }
                HomeFragmentUiState.AllTripDeleteSuccess -> {
                    Toast.makeText(requireContext(), "All items has been deleted",
                        Toast.LENGTH_SHORT).show()
                }
                is HomeFragmentUiState.TripListSuccess -> {
                    binding.rvTrip.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                    parentTripAdapter?.setTripList(state.trips)
                }

                HomeFragmentUiState.TripListDeleteSuccess -> {
                    homeFragmentViewModel.getTripsAndItems()
                }

                HomeFragmentUiState.TripEditSuccess -> {
                    homeFragmentViewModel.getTripsAndItems()
                }
            }
        }

        homeFragmentViewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            when(event) {
                is HomeFragmentViewModelEvent.Error -> {
                    Toast.makeText(requireContext(), event.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onTripMenuClick(trip: TripDetailModel, position: Int, anchorView: View) {
        val view = anchorView
        view?.let {
            val popupMenu = PopupMenu(requireContext(), it,
                0, 0, R.style.CustomPopupMenu)
            popupMenu.inflate(R.menu.trip_menu)

            // Force icons to show
            try {
                val fields = popupMenu.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field.get(popupMenu)
                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.java)
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editTrip -> {
                        val inflater = LayoutInflater.from(requireContext())
                        val dialogView = inflater.inflate(R.layout.dialog_edit_trip, null)

                        val tripNameInput = dialogView.findViewById<EditText>(R.id.edt_trip_name)
                        val tripDateInput = dialogView.findViewById<EditText>(R.id.edt_trip_date)

                        tripNameInput.setText(trip.tripName)
                        tripDateInput.setText(trip.date)

                        tripDateInput.setOnClickListener{
                            val constraintsBuilder = CalendarConstraints.Builder()
                                .setValidator(DateValidatorPointForward.now())

                            val datePicker = MaterialDatePicker.Builder.datePicker()
                                .setTitleText("Select Date")
                                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                                .setCalendarConstraints(constraintsBuilder.build())
                                .build()

                            datePicker.addOnPositiveButtonClickListener { selection ->
                                // Format the selected date
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val formattedDate = dateFormat.format(selection)
                                tripDateInput.setText(formattedDate)
                            }

                            datePicker.show(parentFragmentManager, "DATE_PICKER")
                        }

                        // Create and show the dialog
                        MaterialAlertDialogBuilder(requireContext(),
                            R.style.ThemeOverlay_App_MaterialAlertDialog)
                            .setView(dialogView)
                            .setTitle("Edit Destination")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Save") { dialog, which ->
                                val newTripName = tripNameInput.text.toString()
                                val newTripDate = tripDateInput.text.toString()
                                // Update the trip in your ViewModel
                                if (trip.tripName != newTripName || trip.date != newTripDate ||
                                    newTripName.isNotEmpty()) {

                                    homeFragmentViewModel.editTrip(
                                        tripId = trip.tripId,
                                        tripName = newTripName,
                                        date = newTripDate
                                    )
                                }
                            }
                            .show()
                        true
                    }
                    R.id.deleteTrip -> {
                        homeFragmentViewModel.deleteTrip(trip.tripId)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        } ?: Log.e("TripMenu", "Unable to find view for position $position")
    }
}