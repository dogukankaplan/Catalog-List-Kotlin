package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UrunAdapter(
    private var urunler: List<Urun>,
    private val onItemClick: (Urun) -> Unit,
    private val onDeleteClick: (Urun) -> Unit
) : RecyclerView.Adapter<UrunAdapter.UrunViewHolder>() {

    class UrunViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageUrun: ImageView = view.findViewById(R.id.imageUrun)
        val textModel: TextView = view.findViewById(R.id.textModel)
        val textFiyat: TextView = view.findViewById(R.id.textFiyat)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrunViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_urun, parent, false)
        return UrunViewHolder(view)
    }

    override fun onBindViewHolder(holder: UrunViewHolder, position: Int) {
        val urun = urunler[position]
        holder.textModel.text = urun.model
        holder.textFiyat.text = "$${urun.fiyat}"
        
        // İlk fotoğrafı göster
        val photoList = deserializePhotoList(urun.fotografYolu)
        if (photoList.isNotEmpty()) {
            holder.imageUrun.visibility = View.VISIBLE
            Glide.with(holder.imageUrun)
                .load(photoList[0])
                .centerCrop()
                .into(holder.imageUrun)
        } else {
            holder.imageUrun.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onItemClick(urun) }
        holder.btnDelete.setOnClickListener { onDeleteClick(urun) }
    }

    private fun deserializePhotoList(json: String): List<String> {
        if (json.isEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getItemCount() = urunler.size

    fun updateUrunler(newUrunler: List<Urun>) {
        urunler = newUrunler
        notifyDataSetChanged()
    }
} 