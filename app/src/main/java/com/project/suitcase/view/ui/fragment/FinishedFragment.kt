package com.project.suitcase.view.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.suitcase.databinding.FragmentFinishedBinding
import com.project.suitcase.view.adapter.ItemsListAdapter
import com.project.suitcase.view.viewmodel.FinishedListUiState
import com.project.suitcase.view.viewmodel.FinishedListViewModel
import com.project.suitcase.view.viewmodel.FinishedListViewModelEvent
import com.project.suitcase.view.viewmodel.ItemListViewModel
import com.project.suitcase.view.viewmodel.UpdateItemCheckedStatusViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class FinishedFragment : Fragment() {

    private var binding: FragmentFinishedBinding? = null
    private var finishedListAdapter: ItemsListAdapter? = null

    private val finishedListViewModel: FinishedListViewModel by viewModel()
    private val itemListViewModel: ItemListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        finishedListViewModel.getAllFinishedItemList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterSetUp()

        finishedListAdapter?.onCheckBoxClick = { itemId, tripId, isChecked ->
            itemListViewModel.updateItemCheckedStatus(
                finished = isChecked,
                tripId = tripId,
                itemId = itemId
            )
        }

        finishedListViewModel.uiState.observe(viewLifecycleOwner) {state ->
            when(state){
                FinishedListUiState.Loading -> {
                    binding?.rvFinishedList?.visibility = View.INVISIBLE
                    binding?.progressBarFinished?.visibility = View.VISIBLE
                }
            }
        }
        finishedListViewModel.finishedItemListUiEvent.observe(viewLifecycleOwner) {event ->
            when(event){
                is FinishedListViewModelEvent.Error -> {
                    Toast.makeText(requireContext(), event.error, Toast.LENGTH_SHORT).show()
                }
                is FinishedListViewModelEvent.Success -> {
                    binding?.rvFinishedList?.visibility = View.VISIBLE
                    binding?.progressBarFinished?.visibility = View.INVISIBLE
                    finishedListAdapter?.setItemList(event.itemList)
                }
            }
        }
        itemListViewModel.updateItemCheckedStatusUiEvent.observe(viewLifecycleOwner) { event ->
            when(event){
                is UpdateItemCheckedStatusViewModelEvent.Error -> {
                    Toast.makeText(requireContext(), event.error, Toast.LENGTH_SHORT).show()
                }
                UpdateItemCheckedStatusViewModelEvent.Success -> {
//                    Toast.makeText(requireContext(), "Item has been updated", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun adapterSetUp() {
        finishedListAdapter = ItemsListAdapter()
        binding?.rvFinishedList?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,
                false)
            adapter = finishedListAdapter
        }
    }

}