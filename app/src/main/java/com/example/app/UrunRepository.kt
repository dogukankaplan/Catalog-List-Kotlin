package com.example.app
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class UrunRepository(private val dbHelper: DatabaseHelper) {
    
    fun getUrunById(id: Int): Urun? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "deri_table",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                Urun(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    model = it.getString(it.getColumnIndexOrThrow("model")),
                    fiyat = it.getDouble(it.getColumnIndexOrThrow("fiyat")),
                    fotografYolu = it.getString(it.getColumnIndexOrThrow("fotograf"))
                )
            } else {
                null
            }
        }
    }

    fun urunEkle(urun: Urun): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("model", urun.model)
            put("fiyat", urun.fiyat)
            put("fotograf", urun.fotografYolu)
        }
        return db.insert("deri_table", null, values)
    }

    fun urunGuncelle(urun: Urun): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("model", urun.model)
            put("fiyat", urun.fiyat)
            put("fotograf", urun.fotografYolu)
        }
        return db.update(
            "deri_table",
            values,
            "id = ?",
            arrayOf(urun.id.toString())
        )
    }

    fun urunSil(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("deri_table", "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun tumUrunleriGetir(): List<Urun> {
        val urunler = mutableListOf<Urun>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "deri_table",
            null,
            null,
            null,
            null,
            null,
            "id DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow("id"))
                val model = it.getString(it.getColumnIndexOrThrow("model"))
                val fiyat = it.getDouble(it.getColumnIndexOrThrow("fiyat"))
                val fotograf = it.getString(it.getColumnIndexOrThrow("fotograf"))
                
                urunler.add(Urun(id, model, fiyat, fotograf))
            }
        }
        return urunler
    }
} 