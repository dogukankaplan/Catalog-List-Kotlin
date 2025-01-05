package com.example.app
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class UrunEkleActivity : AppCompatActivity() {
    private lateinit var repository: UrunRepository
    private lateinit var imageUrun: ImageView
    private lateinit var editModel: EditText
    private lateinit var editFiyat: EditText
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).into(imageUrun)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_urun_ekle)

        repository = UrunRepository(DatabaseHelper(this))
        
        imageUrun = findViewById(R.id.imageUrun)
        editModel = findViewById(R.id.editModel)
        editFiyat = findViewById(R.id.editFiyat)

        imageUrun.setOnClickListener {
            getContent.launch("image/*")
        }

        findViewById<Button>(R.id.btnKaydet).setOnClickListener {
            saveUrun()
        }
    }

    private fun saveUrun() {
        val model = editModel.text.toString()
        val fiyat = editFiyat.text.toString().toDoubleOrNull() ?: 0.0
        val fotografYolu = selectedImageUri?.toString() ?: ""

        val yeniUrun = Urun(0, model, fiyat, fotografYolu)
        repository.urunEkle(yeniUrun)
        finish()
    }
} 