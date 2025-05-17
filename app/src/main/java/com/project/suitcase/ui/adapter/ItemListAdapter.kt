package com.project.suitcase.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.suitcase.R
import com.project.suitcase.databinding.ItemItemDetailed2Binding
import com.project.suitcase.domain.model.ItemDetailModel

class ItemsListAdapter : androidx.recyclerview.widget.ListAdapter<ItemDetailModel, ItemsListAdapter.ItemsListViewHolder>(ItemDiffCallback()) {

    lateinit var onCheckBoxClick: ((String, String, Boolean) -> Unit)
    lateinit var onItemClick: ((ItemDetailModel) -> Unit)
    lateinit var onItemDelete: ((ItemDetailModel) -> Unit)

    inner class ItemsListViewHolder(val binding: ItemItemDetailed2Binding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemItemDetailed2Binding.inflate(inflater, parent, false)
        return ItemsListViewHolder(binding)
    }

    fun updateItemList(newList: List<ItemDetailModel>) {
        submitList(newList)
    }


    override fun onBindViewHolder(holder: ItemsListViewHolder, position: Int) {
        val currentItem = getItem(position)
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

            tvDescription.apply {
                visibility = if (currentItem.itemDescription.isBlank()) View.GONE else View.VISIBLE
                text = "Description: ${currentItem.itemDescription}"
            }

            tvLocation.apply {
                visibility = if (currentItem.itemLocation.isBlank()) View.GONE else View.VISIBLE
                text = "Location: ${currentItem.itemLocation}"
            }

            checkBoxItem.isChecked = currentItem.finished == true

            tvItemName.setStrikeThrough(currentItem.finished == true)
            tvItemPrice.setStrikeThrough(currentItem.finished == true)
            tvLocation.setStrikeThrough(currentItem.finished == true)
            tvDescription.setStrikeThrough(currentItem.finished == true)

            checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
                tvItemName.setStrikeThrough(isChecked)
                tvItemPrice.setStrikeThrough(isChecked)
                tvLocation.setStrikeThrough(isChecked)
                tvDescription.setStrikeThrough(isChecked)

                onCheckBoxClick(currentItem.itemId, currentItem.tripId, isChecked)
            }

        }


        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }
}


class ItemDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<ItemDetailModel>() {
    override fun areItemsTheSame(oldItem: ItemDetailModel, newItem: ItemDetailModel): Boolean {
        // Check unique ID
        return oldItem.itemId == newItem.itemId
    }

    override fun areContentsTheSame(oldItem: ItemDetailModel, newItem: ItemDetailModel): Boolean {
        // Compare contents
        return oldItem == newItem
    }
}

fun TextView.setStrikeThrough(enabled: Boolean) {
    paintFlags = if (enabled) {
        paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
    } else {
        paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}