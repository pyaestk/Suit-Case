package com.project.suitcase.view.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.suitcase.databinding.FragmentHomeBinding
import com.project.suitcase.view.adapter.ParentTripAdapter
import com.project.suitcase.view.ui.activity.ItemListActivity
import com.project.suitcase.view.viewmodel.DeleteAllTripViewModelEvent
import com.project.suitcase.view.viewmodel.HomeFragmentUiState
import com.project.suitcase.view.viewmodel.HomeFragmentViewModel
import com.project.suitcase.view.viewmodel.HomeFragmentViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    var parentTripAdapter: ParentTripAdapter? = null
    
    private val homeFragmentViewModel: HomeFragmentViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        homeFragmentViewModel.getTripsAndItems()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentTripAdapter = ParentTripAdapter()

        binding?.rvTrip?.apply {
            layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false)
            adapter = parentTripAdapter
        }
        binding?.btnDeleteAllTrips?.setOnClickListener {
            homeFragmentViewModel.deleteAllTrip()
        }

        parentTripAdapter?.onItemClick = {
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

        homeFragmentViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when(state) {
                HomeFragmentUiState.Loading -> {
                    binding?.rvTrip?.visibility = View.INVISIBLE
                    binding?.progressBar?.visibility = View.VISIBLE
                }
            }
        }
        homeFragmentViewModel.tripListUiEvent.observe(viewLifecycleOwner) { event ->
            when(event) {
                is HomeFragmentViewModelEvent.Error -> {
                    Toast.makeText(requireContext(), event.error, Toast.LENGTH_SHORT).show()
                }
                is HomeFragmentViewModelEvent.Success -> {
                    binding?.rvTrip?.visibility = View.VISIBLE
                    binding?.progressBar?.visibility = View.INVISIBLE
                    parentTripAdapter?.setTripList(event.trips)
                }
            }
        }
        homeFragmentViewModel.deleteAllTripViewModelEvent.observe(viewLifecycleOwner) { event ->
            when(event){
                is DeleteAllTripViewModelEvent.Error -> {
                    Toast.makeText(requireContext(), event.error, Toast.LENGTH_SHORT).show()
                }
                is DeleteAllTripViewModelEvent.Success -> {
                    Toast.makeText(requireContext(), "All items has been deleted",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}