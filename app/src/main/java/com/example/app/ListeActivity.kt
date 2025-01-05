package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class ListeActivity : AppCompatActivity() {
    private lateinit var repository: UrunRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UrunAdapter
    private var originalList = listOf<Urun>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ürün Listesi"

        repository = UrunRepository(DatabaseHelper(this))
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        adapter = UrunAdapter(
            urunler = emptyList(),
            onItemClick = { urun ->
                val intent = Intent(this, UrunDetayActivity::class.java)
                intent.putExtra("urun_id", urun.id)
                startActivity(intent)
            },
            onDeleteClick = { urun ->
                showDeleteConfirmationDialog(urun)
            }
        )
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddProduct).setOnClickListener {
            startActivity(Intent(this, UrunEkleActivity::class.java))
        }

        loadUrunler()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_liste, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterUrunler(newText)
                return true
            }
        })

        return true
    }

    override fun onResume() {
        super.onResume()
        loadUrunler()
    }

    private fun showDeleteConfirmationDialog(urun: Urun) {
        AlertDialog.Builder(this)
            .setTitle("Ürünü Sil")
            .setMessage("${urun.model} ürününü silmek istediğinizden emin misiniz?")
            .setPositiveButton("Sil") { _, _ ->
                deleteUrun(urun)
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun deleteUrun(urun: Urun) {
        val isDeleted = repository.urunSil(urun.id)
        if (isDeleted) {
            loadUrunler()
            Snackbar.make(recyclerView, "${urun.model} başarıyla silindi", Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(recyclerView, "Silme işlemi başarısız oldu", Snackbar.LENGTH_LONG)
                .setBackgroundTint(resources.getColor(android.R.color.holo_red_light, theme))
                .show()
        }
    }

    private fun loadUrunler() {
        originalList = repository.tumUrunleriGetir()
        adapter.updateUrunler(originalList)
    }

    private fun filterUrunler(query: String?) {
        if (query.isNullOrBlank()) {
            adapter.updateUrunler(originalList)
            return
        }

        val filteredList = originalList.filter { urun ->
            urun.model.contains(query, ignoreCase = true)
        }
        adapter.updateUrunler(filteredList)
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