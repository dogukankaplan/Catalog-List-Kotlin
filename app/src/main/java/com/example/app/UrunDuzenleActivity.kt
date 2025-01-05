package com.example.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class UrunDuzenleActivity : AppCompatActivity() {
    private lateinit var repository: UrunRepository
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var editModel: TextInputEditText
    private lateinit var editFiyat: TextInputEditText
    private var urunId: Int = -1

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.clipData?.let { clipData ->
                // Çoklu fotoğraf seçimi
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    processSelectedImage(imageUri)
                }
            } ?: result.data?.data?.let { uri ->
                // Tek fotoğraf seçimi
                processSelectedImage(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_urun_duzenle)

        repository = UrunRepository(DatabaseHelper(this))
        
        // Toolbar'ı ayarla
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (urunId != -1) "Ürün Düzenle" else "Yeni Ürün"

        // Views
        editModel = findViewById(R.id.editModel)
        editFiyat = findViewById(R.id.editFiyat)
        
        // RecyclerView setup
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewImages)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        
        photoAdapter = PhotoAdapter(mutableListOf()) { position ->
            photoAdapter.removePhoto(position)
        }
        recyclerView.adapter = photoAdapter

        // Fotoğraf ekleme butonu
        findViewById<MaterialButton>(R.id.btnAddPhotos).setOnClickListener {
            openImagePicker()
        }

        // Kaydet butonu
        findViewById<MaterialButton>(R.id.btnSave).setOnClickListener {
            saveUrun()
        }

        // Ürün bilgilerini yükle
        urunId = intent.getIntExtra("urun_id", -1)
        if (urunId != -1) {
            loadUrun(urunId)
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        getContent.launch(Intent.createChooser(intent, "Fotoğraf Seç"))
    }

    private fun processSelectedImage(uri: Uri) {
        // Arka planda resmi sıkıştır ve kaydet
        Thread {
            val compressedImagePath = ImageUtils.compressAndSaveImage(this, uri)
            runOnUiThread {
                compressedImagePath?.let {
                    photoAdapter.addPhoto(it)
                }
            }
        }.start()
    }

    private fun loadUrun(id: Int) {
        repository.getUrunById(id)?.let { urun ->
            editModel.setText(urun.model)
            editFiyat.setText(urun.fiyat.toString())
            
            // Önce mevcut fotoğrafları temizle
            photoAdapter.clear()
            
            // Fotoğrafları yükle
            val photoList = deserializePhotoList(urun.fotografYolu)
            photoList.forEach { photoPath ->
                photoAdapter.addPhoto(photoPath)
            }
        }
    }

    private fun saveUrun() {
        val model = editModel.text.toString()
        val fiyatStr = editFiyat.text.toString()

        if (model.isEmpty() || fiyatStr.isEmpty()) {
            Toast.makeText(this, "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        val fiyat = fiyatStr.toDoubleOrNull()
        if (fiyat == null) {
            Toast.makeText(this, "Geçerli bir fiyat girin", Toast.LENGTH_SHORT).show()
            return
        }

        val photos = photoAdapter.getPhotos()
        val serializedPhotos = serializePhotoList(photos)

        val urun = Urun(
            id = if (urunId != -1) urunId else 0,
            model = model,
            fiyat = fiyat,
            fotografYolu = serializedPhotos
        )

        if (urunId != -1) {
            repository.urunGuncelle(urun)
        } else {
            repository.urunEkle(urun)
        }

        finish()
    }

    private fun serializePhotoList(photos: List<String>): String {
        return Gson().toJson(photos)
    }

    private fun deserializePhotoList(json: String): List<String> {
        if (json.isEmpty()) return emptyList()
        val listType: Type = object : TypeToken<List<String>>() {}.type
        return try {
            Gson().fromJson(json, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
} 