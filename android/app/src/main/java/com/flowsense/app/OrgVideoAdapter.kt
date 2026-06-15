package com.flowsense.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class OrgVideoItem(
    val id: Int,
    val title: String,
    val date: String,
    val path: String
)

class OrgVideoAdapter(
    private var items: List<OrgVideoItem>,
    private val onPlayClick: (OrgVideoItem) -> Unit,
    private val onDeleteClick: (OrgVideoItem) -> Unit
) : RecyclerView.Adapter<OrgVideoAdapter.ViewHolder>() {

    fun updateItems(newItems: List<OrgVideoItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvVideoTitle)
        val tvDate: TextView = view.findViewById(R.id.tvVideoDate)
        val btnPlay: ImageView = view.findViewById(R.id.btnPlayVideo)
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteVideo)

        fun bind(item: OrgVideoItem) {
            tvTitle.text = item.title
            tvDate.text = item.date

            btnPlay.setOnClickListener {
                onPlayClick(item)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_org_video, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
