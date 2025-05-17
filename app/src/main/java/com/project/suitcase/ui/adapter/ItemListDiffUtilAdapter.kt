package com.project.suitcase.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    private var itemList: List<ItemDetailModel> = listOf()

    fun getItemList(): List<ItemDetailModel> {
        return itemList
    }

    fun setItemList(newItemList: List<ItemDetailModel>){
        val diffCallback = itemDetailListDiffCallback(this.itemList, newItemList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.itemList = newItemList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListDIffUtilViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemItemDetailed2Binding.inflate(inflater, parent, false)
        return ItemListDIffUtilViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemListDIffUtilViewHolder, position: Int) {
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
            onItemClick.invoke(currentItem)
        }

    }

    fun TextView.setStrikeThrough(enabled: Boolean) {
        paintFlags = if (enabled) {
            paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

}

class itemDetailListDiffCallback(
    private val oldList: List<ItemDetailModel>,
    private val newList: List<ItemDetailModel>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].itemId == newList[newItemPosition].itemId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}