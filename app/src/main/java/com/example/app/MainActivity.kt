package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        findViewById<Button>(R.id.btnListe).setOnClickListener {
            startActivity(Intent(this, ListeActivity::class.java))
        }

        findViewById<Button>(R.id.btnAyarlar).setOnClickListener {
            startActivity(Intent(this, AyarlarActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_product -> {
                startActivity(Intent(this, UrunEkleActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}