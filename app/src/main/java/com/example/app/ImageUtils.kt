package com.example.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

object ImageUtils {
    private const val MAX_WIDTH = 1024
    private const val MAX_HEIGHT = 1024
    private const val QUALITY = 80

    fun compressAndSaveImage(context: Context, imageUri: Uri): String? {
        try {
            // Uri'den input stream al
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                // Bitmap'i boyutlarını kontrol ederek yükle
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)
                
                // Yeniden boyutlandırma oranını hesapla
                val scale = calculateScale(options.outWidth, options.outHeight)
                
                // Bitmap'i yeni boyutlarıyla yükle
                context.contentResolver.openInputStream(imageUri)?.use { newInputStream ->
                    val bitmap = BitmapFactory.Options().run {
                        inSampleSize = scale
                        inJustDecodeBounds = false
                        BitmapFactory.decodeStream(newInputStream, null, this)
                    }

                    bitmap?.let {
                        // Sıkıştırılmış dosyayı kaydet
                        val file = File(context.getExternalFilesDir(null), 
                            "IMG_${UUID.randomUUID()}.jpg")
                        
                        FileOutputStream(file).use { out ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, out)
                        }
                        
                        bitmap.recycle()
                        return file.absolutePath
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun calculateScale(width: Int, height: Int): Int {
        var scale = 1
        while (width / scale > MAX_WIDTH || height / scale > MAX_HEIGHT) {
            scale *= 2
        }
        return scale
    }
} 