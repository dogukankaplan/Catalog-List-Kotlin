package com.example.app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoAdapter(
    private var photos: MutableList<String>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        
        Glide.with(holder.imageView)
            .load(photo)
            .centerCrop()
            .into(holder.imageView)

        holder.btnDelete.setOnClickListener {
            onDeleteClick(position)
        }

        holder.imageView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PhotoDetailActivity::class.java)
            intent.putExtra("photo_path", photo)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = photos.size

    fun addPhoto(photoPath: String) {
        if (!photos.contains(photoPath)) {  // Aynı fotoğrafı tekrar eklemeyi önle
            photos.add(photoPath)
            notifyItemInserted(photos.size - 1)
        }
    }

    fun removePhoto(position: Int) {
        if (position in photos.indices) {
            photos.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        val size = photos.size
        photos.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun getPhotos(): List<String> = photos.toList()
} 