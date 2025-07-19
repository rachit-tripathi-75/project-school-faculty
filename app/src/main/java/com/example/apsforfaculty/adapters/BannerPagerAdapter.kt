package com.example.apsforfaculty.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.models.BannerItem
import com.google.android.material.card.MaterialCardView

class BannerPagerAdapter(private val bannerList: List<BannerItem>) : RecyclerView.Adapter<BannerPagerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bannerCard: MaterialCardView = itemView.findViewById(R.id.banner_card)
        val bannerImageView: ImageView = itemView.findViewById(R.id.banner_image_view)
        val bannerTitleTextView: TextView = itemView.findViewById(R.id.banner_title_text_view)
        val bannerSubtitleTextView: TextView = itemView.findViewById(R.id.banner_subtitle_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.banner_item, parent, false)
        return BannerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val currentItem = bannerList[position]
        holder.bannerImageView.setImageResource(currentItem.imageResId)
        holder.bannerTitleTextView.text = currentItem.title
        holder.bannerSubtitleTextView.text = currentItem.subtitle

        // Optional: Set a transition name if you're using shared element transitions
        holder.bannerCard.transitionName = "banner_transition_$position"
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }
}