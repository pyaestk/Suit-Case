package com.project.suitcase.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.suitcase.databinding.ItemItemDetailed2Binding
import com.project.suitcase.domain.model.ItemDetailModel
import java.util.Locale

class SearchResultAdapter:
    RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>(), Filterable {

    private var itemList: List<ItemDetailModel> = listOf()
    private var filteredItemList: List<ItemDetailModel> = listOf()


    lateinit var onCheckBoxClick: ((String, String, Boolean) -> Unit)
    lateinit var onItemClick: ((ItemDetailModel) -> Unit)

    @SuppressLint("NotifyDataSetChanged")
    fun setItemList(itemList: List<ItemDetailModel>){
        this.itemList = itemList
        notifyDataSetChanged()
    }
    inner class SearchResultViewHolder(val binding: ItemItemDetailed2Binding):
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemItemDetailed2Binding.inflate(inflater, parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val currentItem = filteredItemList[position]
        holder.binding.apply {
            Glide.with(holder.itemView)
                .load(currentItem.itemImage)
                .into(ivItem)

            tvItemName.text = currentItem.itemName

            tvItemPrice.text = if (currentItem.itemPrice.isBlank()) {
                "Price: Unknown"
            } else {
                "Price: $${currentItem.itemPrice}"
            }

            if (currentItem.itemDescription.isBlank()){
                tvDescription.visibility = View.GONE
            } else {
                tvDescription.text = "Description: ${currentItem.itemDescription}"
            }
            if (currentItem.itemLocation.isBlank()){
                tvLocation.visibility = View.GONE
            } else {
                tvLocation.text = "Location: ${currentItem.itemLocation}"
            }

            checkBoxItem.isChecked = if (currentItem.finished == true) true else false
        }
        holder.binding.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
            onCheckBoxClick(currentItem.itemId, currentItem.tripId, isChecked)
        }
        holder.itemView.setOnClickListener {
//            onItemClick.invoke(currentItem)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchText = constraint.toString().toLowerCase(Locale.getDefault())
                filteredItemList = if (searchText.isEmpty()) {
                    itemList
                } else {
                    val filteredList = ArrayList<ItemDetailModel>()
                    for (item in itemList) {
                        if (item.itemName.contains(searchText) || item.itemLocation.contains(searchText)
                            || item.itemDescription.contains(searchText)) {
                            filteredList.add(item)
                        }
                    }
                    filteredList
                }
                val filteredResults = FilterResults()
                filteredResults.values = filteredItemList
                return filteredResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItemList = results?.values as ArrayList<ItemDetailModel>
                notifyDataSetChanged()
            }

        }
    }
}