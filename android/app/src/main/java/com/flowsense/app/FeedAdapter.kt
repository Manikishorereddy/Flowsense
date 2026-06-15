package com.flowsense.app

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.flowsense.app.network.ApiClient

data class FeedItem(
    val title: String,
    val count: String,
    val density: String,
    val showHighDensityAlert: Boolean,
    val videoUrl: String
)

class FeedAdapter(
    private var items: List<FeedItem>,
    private val onItemClick: (FeedItem) -> Unit
) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvLocationTitle)
        val tvAlertTag: TextView = view.findViewById(R.id.tvAlertTag)
        val vvFeedVideo: VideoView = view.findViewById(R.id.vvFeedVideo)

        init {
            view.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(items[adapterPosition])
                }
            }
        }

        fun bind(item: FeedItem) {
            tvTitle.text = item.title
            
            if (item.showHighDensityAlert) {
                tvAlertTag.visibility = View.VISIBLE
            } else {
                tvAlertTag.visibility = View.GONE
            }

            if (item.videoUrl.isNotEmpty()) {
                val fullUrl = (ApiClient.BASE_URL.replace("api/", "") + item.videoUrl).replace(" ", "%20")
                vvFeedVideo.setVideoURI(Uri.parse(fullUrl))
                vvFeedVideo.setOnPreparedListener { mp ->
                    mp.isLooping = true
                    mp.setVolume(0f, 0f) // Mute to allow autoplay
                    vvFeedVideo.start()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_feed_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<FeedItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
