package com.flowsense.app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class LocationItem(
    val name: String,
    val typeAndDistance: String,
    val status: String,
    val waitTime: String,
    val accuracy: String,
    val videoPath: String = ""
)

class LocationAdapter(
    private var items: List<LocationItem>,
    private val onItemClick: ((LocationItem) -> Unit)? = null
) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    fun updateItems(newItems: List<LocationItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvLocationName)
        val tvDetails: TextView = view.findViewById(R.id.tvLocationDetails)
        val tvWaitTime: TextView = view.findViewById(R.id.tvWaitTime)
        val tvAccuracy: TextView = view.findViewById(R.id.tvAccuracy)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(items[position])
                }
            }
        }

        fun bind(item: LocationItem) {
            tvName.text = item.name
            tvDetails.text = item.typeAndDistance
            tvWaitTime.text = item.waitTime
            tvAccuracy.text = item.accuracy
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_location_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
