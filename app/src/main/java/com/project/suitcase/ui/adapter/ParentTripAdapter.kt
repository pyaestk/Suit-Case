package com.project.suitcase.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.suitcase.R
import com.project.suitcase.databinding.ItemTripBinding
import com.project.suitcase.domain.model.ItemDetailModel
import com.project.suitcase.domain.model.TripDetailModel

class ParentTripAdapter: RecyclerView.Adapter<ParentTripAdapter.TripListViewHolder>() {

    private var tripList: List<TripDetailModel> = listOf()
    private var onTripMenuClickListener: OnTripMenuClickListener? = null

    fun setTripList(newTripList: List<TripDetailModel>) {
        val diffCallback = TripDiffCallback(this.tripList, newTripList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.tripList = newTripList
        diffResult.dispatchUpdatesTo(this)
    }

    interface OnTripMenuClickListener {
        fun onTripMenuClick(trip: TripDetailModel, position: Int)
    }

    lateinit var onItemClick: ((TripDetailModel) -> Unit)

    lateinit var onChildItemClick : ((ItemDetailModel) -> Unit)

    inner class TripListViewHolder(val binding: ItemTripBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTripBinding.inflate(inflater, parent, false)
        return TripListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    fun setOnTripMenuClickListener(listener: OnTripMenuClickListener) {
        this.onTripMenuClickListener = listener
    }

    override fun onBindViewHolder(holder: TripListViewHolder, position: Int) {
        val currentTrip = tripList[position]
        holder.binding.apply {
            tvTripName.text = currentTrip.tripName
            tvTripDate.text = currentTrip.date
            tvItemCount.text = holder.itemView.context.getString(R.string.tv_item_count,
                currentTrip.items.size)
        }

        val childItemAdapter = ChildItemAdapter().apply {
            onItemClick = {
                onChildItemClick.invoke(it)
            }
        }
        holder.binding.rvItemList.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.binding.rvItemList.adapter = childItemAdapter

        childItemAdapter.setItemList(currentTrip.items)

        holder.itemView.setOnClickListener {
            onItemClick.invoke(currentTrip)
        }

        holder.binding.btnTripMenu.setOnClickListener {
            Log.d("ParentTripAdapter", "Menu button clicked at position: $position")
            onTripMenuClickListener?.onTripMenuClick(currentTrip, position)
        }
    }
}

class TripDiffCallback(
    private val oldList: List<TripDetailModel>,
    private val newList: List<TripDetailModel>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].tripId == newList[newItemPosition].tripId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}