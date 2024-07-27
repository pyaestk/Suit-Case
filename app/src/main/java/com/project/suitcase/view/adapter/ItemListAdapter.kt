package com.project.suitcase.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.suitcase.databinding.ItemItemDetailed2Binding
import com.project.suitcase.domain.model.ItemDetailModel

class ItemsListAdapter: RecyclerView.Adapter<ItemsListAdapter.ItemsListViewHolder>(){

    private var itemList: List<ItemDetailModel> = listOf()

    lateinit var onCheckBoxClick: ((String, String, Boolean) -> Unit)
    lateinit var onItemClick: ((ItemDetailModel) -> Unit)

    @SuppressLint("NotifyDataSetChanged")
    fun setItemList(itemList: List<ItemDetailModel>){
        this.itemList = itemList
        notifyDataSetChanged()
    }
    inner class ItemsListViewHolder(val binding: ItemItemDetailed2Binding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemItemDetailed2Binding.inflate(inflater, parent, false)
        return ItemsListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemsListViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.binding.apply {
            Glide.with(holder.itemView)
                .load(currentItem.itemImage)
                .into(ivItem)
            tvItemName.text = currentItem.itemName
            tvItemPrice.text = currentItem.itemPrice
            checkBoxItem.isChecked = if (currentItem.finished == true) true else false
        }
        holder.binding.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
            onCheckBoxClick(currentItem.itemId, currentItem.tripId, isChecked)
        }
        holder.itemView.setOnClickListener {
            onItemClick.invoke(currentItem)
        }

    }
}