package com.project.suitcase.view.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.suitcase.databinding.FragmentHomeBinding
import com.project.suitcase.view.adapter.TripAdapter
import com.project.suitcase.view.ui.activity.ItemListActivity
import com.project.suitcase.view.viewmodel.GetTripViewModelEvent
import com.project.suitcase.view.viewmodel.TripUiState
import com.project.suitcase.view.viewmodel.TripViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    var tripAdapter: TripAdapter? = null

    private val viewModel: TripViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTripsAndItems()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tripAdapter = TripAdapter()

        binding?.rvTrip?.apply {
            layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false)
            adapter = tripAdapter
        }

        tripAdapter?.onItemClick = {
            val intent = Intent(requireContext(), ItemListActivity::class.java).apply {
                putExtra("tripId", it.tripId)
                putExtra("tripName", it.tripName)
                putExtra("tripDate", it.date)
            }
            startActivity(intent)
        }
        
        viewModelSetUp()

    }

    private fun viewModelSetUp() {
        viewModel.uiState.observe(viewLifecycleOwner){ state ->
            when(state){
                TripUiState.Loading -> {

                }
            }
        }
        viewModel.tripListUiEvent.observe(viewLifecycleOwner){ event ->
            when(event){
                is GetTripViewModelEvent.Error -> {
                    Log.e("GetTrip", event.error)
                    Toast.makeText(requireContext(), event.error, Toast.LENGTH_SHORT).show()
                }
                is GetTripViewModelEvent.Success -> {
                    tripAdapter?.setTripList(event.trips)
                }
            }
        }
    }

}