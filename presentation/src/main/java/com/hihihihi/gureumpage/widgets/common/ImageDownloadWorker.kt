package com.hihihihi.gureumpage.widgets.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@HiltWorker
class ImageDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val KEY_IMAGE_URL = "image_url"
        const val KEY_BITMAP_PATH = "bitmap_path"
        const val KEY_SUCCESS = "success"

    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val imageUrl = inputData.getString(KEY_IMAGE_URL) ?: return@withContext Result.failure()

            val bitmap = downloadImage(imageUrl)
            if (bitmap == null) {
                return@withContext Result.failure()
            }

            val cacheFile = saveBitmapToCache(bitmap, imageUrl)
            if (cacheFile == null) {
                return@withContext Result.failure()
            }

            val outputData = workDataOf(
                KEY_BITMAP_PATH to cacheFile.absolutePath,
                KEY_SUCCESS to true
            )

            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun downloadImage(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val inputStream = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap, imageUrl: String): File? {
        return try {
            val cacheDir = applicationContext.cacheDir
            val imageDir = File(cacheDir, "widget_images")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }

            val fileName = imageUrl.hashCode().toString() + ".png"
            val imageFile = File(imageDir, fileName)

            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()

            imageFile
        } catch (e: Exception) {
            null
        }
    }
}