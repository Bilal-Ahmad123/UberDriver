package com.example.uberdriver.presentation.auth.login.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.uberdriver.R
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(context: Context?, sliderDataArrayList: List<SliderItem>) :
    SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder>() {
    private val mSliderItems: List<SliderItem> =
        sliderDataArrayList
    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterViewHolder {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.slider_layout, null)
        return SliderAdapterViewHolder(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterViewHolder, position: Int) {
        val sliderItem: SliderItem = mSliderItems[position]
        Glide.with(viewHolder.itemView)
            .load(sliderItem.imgUrl)
            .fitCenter()
            .into(viewHolder.imageViewBackground)
    }
    override fun getCount(): Int {
        return mSliderItems.size
    }

    class SliderAdapterViewHolder(itemView: View) : ViewHolder(itemView) {
        private var itemView1: View = itemView
        var imageViewBackground: ImageView = itemView.findViewById<ImageView>(R.id.myimage)
    }
}