package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UrunDetayActivity : AppCompatActivity() {
    private lateinit var repository: UrunRepository
    private lateinit var photoAdapter: PhotoAdapter
    private var urunId: Int = -1
    private var currentUrun: Urun? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_urun_detay)

        repository = UrunRepository(DatabaseHelper(this))

        // Toolbar'ı ayarla
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ürün Detayı"

        // RecyclerView setup
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewImages)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        
        photoAdapter = PhotoAdapter(mutableListOf()) { _ -> 
            // Detay ekranında silme işlemi yok
        }
        recyclerView.adapter = photoAdapter

        // Ürün bilgilerini yükle
        urunId = intent.getIntExtra("urun_id", -1)
        if (urunId != -1) {
            loadUrun(urunId)
        }
    }

    private fun loadUrun(id: Int) {
        repository.getUrunById(id)?.let { urun ->
            currentUrun = urun
            
            findViewById<TextView>(R.id.textModel).text = urun.model
            findViewById<TextView>(R.id.textFiyat).text = String.format("$%.2f", urun.fiyat)
            
            // Fotoğrafları yükle
            val photoList = deserializePhotoList(urun.fotografYolu)
            photoList.forEach { photoPath ->
                photoAdapter.addPhoto(photoPath)
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_urun_detay, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_edit -> {
                val intent = Intent(this, UrunDuzenleActivity::class.java)
                intent.putExtra("urun_id", urunId)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        // Sayfa tekrar açıldığında güncel verileri göster
        if (urunId != -1) {
            loadUrun(urunId)
        }
    }
} 