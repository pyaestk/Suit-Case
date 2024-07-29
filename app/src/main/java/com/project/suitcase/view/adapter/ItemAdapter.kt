package com.project.suitcase.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.suitcase.databinding.ItemItemsBinding
import com.project.suitcase.domain.model.ItemDetailModel

class ItemAdapter: RecyclerView.Adapter<ItemAdapter.ItemListViewHolder>(){

    private var itemList: List<ItemDetailModel> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setItemList(itemList: List<ItemDetailModel>){
        this.itemList = itemList
        notifyDataSetChanged()
    }
    inner class ItemListViewHolder(val binding: ItemItemsBinding):
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemItemsBinding.inflate(inflater, parent, false)
        return ItemListViewHolder(binding)
    }

    override fun getItemCount(): Int {
         return itemList.size
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val currentItem = itemList[position]
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


        }

    }
}