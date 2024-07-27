package com.project.suitcase.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.suitcase.R
import com.project.suitcase.databinding.ItemTripBinding
import com.project.suitcase.domain.model.TripDetailModel

class TripAdapter(

): RecyclerView.Adapter<TripAdapter.TripListViewHolder>() {

    private var tripList: List<TripDetailModel> = listOf()
            
    @SuppressLint("NotifyDataSetChanged")
    fun setTripList(tripList: List<TripDetailModel>){
        this.tripList = tripList
        notifyDataSetChanged()
    }

    lateinit var onItemClick: ((TripDetailModel) -> Unit)

    inner class TripListViewHolder(val binding: ItemTripBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTripBinding.inflate(inflater, parent, false)
        return TripListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    override fun onBindViewHolder(holder: TripListViewHolder, position: Int) {
        val currentTrip = tripList[position]
        holder.binding.apply {
            tvTripName.text = currentTrip.tripName
            tvTripDate.text = currentTrip.date
            tvItemCount.text = holder.itemView.context.getString(R.string.tv_item_count,
                currentTrip.items.size)
        }

        val itemAdapter = ItemAdapter()
        holder.binding.rvItemList.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.binding.rvItemList.adapter = itemAdapter

        itemAdapter.setItemList(currentTrip.items)

        holder.itemView.setOnClickListener {
            onItemClick.invoke(currentTrip)
        }
    }
}