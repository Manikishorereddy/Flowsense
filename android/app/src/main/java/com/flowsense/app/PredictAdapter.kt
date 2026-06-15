package com.flowsense.app

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class PredictItem(
    val title: String,
    val subtitle: String,
    val colorHex: String,
    val videoPath: String
)

class PredictAdapter(
    private var items: List<PredictItem>,
    private val onItemClick: (PredictItem) -> Unit
) : RecyclerView.Adapter<PredictAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPredictTitle: TextView = view.findViewById(R.id.tvPredictTitle)
        val tvPredictSubtitle: TextView = view.findViewById(R.id.tvPredictSubtitle)

        init {
            view.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(items[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_predict, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvPredictTitle.text = item.title
        holder.tvPredictSubtitle.text = item.subtitle
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<PredictItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
