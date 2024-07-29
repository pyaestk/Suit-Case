package com.project.suitcase.view.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.suitcase.databinding.FragmentSearchBinding
import com.project.suitcase.view.adapter.SearchResultAdapter
import com.project.suitcase.view.viewmodel.ItemListViewModel
import com.project.suitcase.view.viewmodel.SearchListViewModelEvent
import com.project.suitcase.view.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private var binding: FragmentSearchBinding? = null
    private val searchViewModel: SearchViewModel by viewModel()
    private val itemListViewModel: ItemListViewModel by viewModel()
    private var searchResultAdapter: SearchResultAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        searchViewModel.getAllItems()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAdapter()

        searchResultAdapter?.onCheckBoxClick = { itemId, tripId, isChecked ->

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

        binding?.searchbar?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()){

                } else {
                    searchResultAdapter?.filter?.filter(newText)
                }
                return true
            }

        })

        searchViewModel.itemListUiEvent.observe(viewLifecycleOwner) { event ->
            when(event) {
                is SearchListViewModelEvent.Error -> {
                    Toast.makeText(requireContext(), event.error, Toast.LENGTH_SHORT).show()
                }
                is SearchListViewModelEvent.Success -> {
                    searchResultAdapter?.setItemList(event.itemList)
                }
            }
        }

    }

    private fun setUpAdapter() {
        searchResultAdapter = SearchResultAdapter()
        binding?.rvSearchResult?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchResultAdapter
        }
    }

}