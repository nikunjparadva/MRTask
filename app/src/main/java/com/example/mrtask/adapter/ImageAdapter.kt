package com.example.mrtask.adapter

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mrtask.R
import com.example.mrtask.databinding.ItemUploadBinding
import com.squareup.picasso.Picasso

class ImageAdapter(var context: Context, var imagelist: MutableList<Uri>) :
    RecyclerView.Adapter<ImageAdapter.Holder>() {
    class Holder(itemView: ItemUploadBinding) : RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var binding = ItemUploadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Picasso.get().load(imagelist[position]).placeholder(R.color.grey).into(holder.binding.ivImage)
    }

    override fun getItemCount(): Int {
        return imagelist.size
    }
}