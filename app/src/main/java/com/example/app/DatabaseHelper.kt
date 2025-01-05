package com.example.app
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DERI.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "deri_table"
        private const val COLUMN_ID = "id"
        private const val COLUMN_MODEL = "model"
        private const val COLUMN_FIYAT = "fiyat"
        private const val COLUMN_FOTOGRAF = "fotograf"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MODEL TEXT NOT NULL,
                $COLUMN_FIYAT REAL NOT NULL,
                $COLUMN_FOTOGRAF TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}