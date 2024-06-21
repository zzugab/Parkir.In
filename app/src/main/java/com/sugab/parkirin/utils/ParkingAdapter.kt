package com.sugab.parkirin.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sugab.parkirin.R
import com.sugab.parkirin.data.parking.Parking

class ParkingAdapter(private var items: List<Parking>) : RecyclerView.Adapter<ParkingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvParkingName: TextView = itemView.findViewById(R.id.tv_parkingName)
        private val tvParkingId: TextView = itemView.findViewById(R.id.tv_parkingId)
        private val tvPlat: TextView = itemView.findViewById(R.id.tv_plat)
        private val tvStartTime: TextView = itemView.findViewById(R.id.tv_startTime)

        fun bind(item: Parking) {
            tvParkingName.text = item.namePlaced
            tvParkingId.text = item.id.toString()
            tvPlat.text = item.plat
            tvStartTime.text = item.startTime?.toDate().toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.parking_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Parking>) {
        items = newItems
        notifyDataSetChanged()
    }
}
