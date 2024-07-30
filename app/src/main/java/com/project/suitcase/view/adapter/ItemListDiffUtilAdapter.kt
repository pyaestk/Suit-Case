package com.project.suitcase.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.suitcase.R
import com.project.suitcase.databinding.ItemItemDetailed2Binding
import com.project.suitcase.domain.model.ItemDetailModel

class ItemListDiffUtilAdapter: RecyclerView.Adapter<ItemListDiffUtilAdapter.ItemListDIffUtilViewHolder>() {

    lateinit var onCheckBoxClick: ((String, String, Boolean) -> Unit)
    lateinit var onItemClick: ((ItemDetailModel) -> Unit)
    inner class ItemListDIffUtilViewHolder(val binding: ItemItemDetailed2Binding):
            RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<ItemDetailModel>() {
        override fun areItemsTheSame(oldItem: ItemDetailModel, newItem: ItemDetailModel): Boolean {
            return oldItem.itemId == newItem.itemImage
        }

        override fun areContentsTheSame(
            oldItem: ItemDetailModel,
            newItem: ItemDetailModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListDIffUtilViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemItemDetailed2Binding.inflate(inflater, parent, false)
        return ItemListDIffUtilViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ItemListDIffUtilViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        holder.binding.apply {
            if (currentItem.itemImage == null) {
                ivItem.setImageResource(R.drawable.image_icon)
            } else {
                Glide.with(holder.itemView)
                    .load(currentItem.itemImage)
                    .into(ivItem)
                ivItem.setBackgroundResource(R.color.white)
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
//            onItemClick.invoke(currentItem)
        }

    }
    
}