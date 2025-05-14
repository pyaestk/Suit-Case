package com.project.suitcase.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.suitcase.R
import com.project.suitcase.databinding.ItemItemsBinding
import com.project.suitcase.domain.model.ItemDetailModel

class ChildItemAdapter: RecyclerView.Adapter<ChildItemAdapter.ItemListViewHolder>(){

    private var itemList: List<ItemDetailModel> = listOf()

    lateinit var onItemClick: ((ItemDetailModel) -> Unit)

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

            if (currentItem.itemImage.isNullOrEmpty()) {
                ivItem.setImageResource(R.drawable.photo)
                ivItem.maxHeight = 20
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

        }
        holder.itemView.setOnClickListener {
            onItemClick.invoke(currentItem)
        }

    }
}