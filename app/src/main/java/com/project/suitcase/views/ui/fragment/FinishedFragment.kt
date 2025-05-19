package com.project.suitcase.views.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.suitcase.R
import com.project.suitcase.databinding.FragmentFinishedBinding
import com.project.suitcase.views.adapter.ItemsListAdapter
import com.project.suitcase.views.viewmodel.FinishedListUiState
import com.project.suitcase.views.viewmodel.FinishedListViewModel
import com.project.suitcase.views.viewmodel.FinishedListViewModelEvent
import com.project.suitcase.views.ui.activity.item.ItemDetailActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class FinishedFragment : Fragment() {

    private var binding: FragmentFinishedBinding? = null
    private var finishedListAdapter: ItemsListAdapter? = null

    private val finishedListViewModel: FinishedListViewModel by viewModel()

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
        
        binding?.btnDeleteAllTrips?.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle("Delete all finished items?")
                .setMessage("All finished items will be from the list.")
                .setNegativeButton("NO") { dialog, which ->
                    //nothing
                }
                .setPositiveButton("YES") { dialog, which ->
                    finishedListViewModel.deleteAllFinishedItemList()
                }
                .show()
        }

        finishedListAdapter?.onCheckBoxClick = { itemId, tripId, isChecked ->
            finishedListViewModel.updateItemCheckedStatus(
                finished = isChecked,
                tripId = tripId,
                itemId = itemId
            )
        }

        finishedListAdapter?.onItemClick = {
            val intent = Intent(requireContext(), ItemDetailActivity::class.java).apply {
                putExtra("itemID", it.itemId)
                putExtra("tripID", it.tripId)
                putExtra("itemImage", it.itemImage)
            }
            startActivity(intent)
        }
        viewModelSetup()

    }

    private fun viewModelSetup() {
        finishedListViewModel.uiState.observe(viewLifecycleOwner) {state ->
            when(state){
                FinishedListUiState.Loading -> {
                    binding?.rvFinishedList?.visibility = View.INVISIBLE
                    binding?.progressBarFinished?.visibility = View.VISIBLE
                    binding?.emptyLayout?.visibility = View.INVISIBLE
                }

                FinishedListUiState.DeleteAllSuccess -> {
                    binding?.rvFinishedList?.visibility = View.VISIBLE
                    binding?.progressBarFinished?.visibility = View.INVISIBLE
                }
                is FinishedListUiState.Success -> {
                    binding?.rvFinishedList?.visibility = View.VISIBLE
                    binding?.progressBarFinished?.visibility = View.INVISIBLE
                    finishedListAdapter?.updateItemList(state.itemList)
                    if (state.itemList.size < 1) {
                        binding?.emptyLayout?.visibility = View.VISIBLE
                    } else {
                        binding?.emptyLayout?.visibility = View.INVISIBLE
                    }
                }

                FinishedListUiState.UpdateSuccess -> {

                }
            }
        }
        finishedListViewModel.finishedItemListUiEvent.observe(viewLifecycleOwner) { event ->
            when(event) {
                is FinishedListViewModelEvent.Error -> {
                    Toast.makeText(requireContext(), event.error, Toast.LENGTH_SHORT).show()
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