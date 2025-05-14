package com.project.suitcase.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.suitcase.R
import com.project.suitcase.databinding.ItemItemDetailed2Binding
import com.project.suitcase.domain.model.ItemDetailModel

class ItemsListAdapter: RecyclerView.Adapter<ItemsListAdapter.ItemsListViewHolder>(){

    private var itemList: MutableList<ItemDetailModel> = mutableListOf()
    lateinit var onCheckBoxClick: ((String, String, Boolean) -> Unit)
    lateinit var onItemClick: ((ItemDetailModel) -> Unit)
    lateinit var onItemDelete: ((ItemDetailModel) -> Unit)

    @SuppressLint("NotifyDataSetChanged")
    fun setItemList(itemList: List<ItemDetailModel>){
        this.itemList = itemList.toMutableList()
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        val item = itemList[position]
        onItemDelete(item)
        itemList.removeAt(position)
        notifyItemRemoved(position)
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
            if (currentItem.itemImage.isNullOrEmpty()) {
                ivItem.setImageResource(R.drawable.photo)
            } else {
                Glide.with(holder.itemView)
                    .load(currentItem.itemImage)
                    .into(ivItem)
            }

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

            checkBoxItem.isChecked = currentItem.finished == true
        }
        holder.binding.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
            onCheckBoxClick(currentItem.itemId, currentItem.tripId, isChecked)
        }
        holder.itemView.setOnClickListener {
            onItemClick.invoke(currentItem)
        }

    }


}