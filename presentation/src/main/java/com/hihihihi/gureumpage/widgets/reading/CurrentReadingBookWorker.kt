package com.hihihihi.gureumpage.widgets.reading

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.usecase.userbook.GetUserBooksByStatusUseCase
import com.hihihihi.gureumpage.widgets.common.ImageDownloadWorker
import com.hihihihi.gureumpage.widgets.common.WidgetBook
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class CurrentReadingBookWidgetWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getUserBookByStatus: GetUserBooksByStatusUseCase
) : CoroutineWorker(appContext, params) {

    companion object {
        const val UNIQUE_WORK_NAME = "CurrentReadingBookWorker"
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUid: String
        get() = auth.currentUser!!.uid

    override suspend fun doWork(): Result {

        try {

            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceId = manager.getGlanceIds(CurrentReadingBookWidget::class.java)

            val book = getUserBookByStatus(currentUid, ReadingStatus.READING).first().firstOrNull()

            book?.imageUrl?.let { imageUrl ->
                if (imageUrl.isNotBlank()) {
                    scheduleImageDownload(imageUrl, applicationContext)
                }
            }
            Log.d("Widget","book:$book")
            glanceId.forEach { id ->
                val widgetBook = WidgetBook(
                    title = book?.title ?: "읽고 있는 책 없음",
                    author = book?.author.orEmpty(),
                    bookId = book?.userBookId.orEmpty(),
                    imageUrl = book?.imageUrl.orEmpty()
                )

                val jsonData = Gson().toJson(widgetBook)

                updateAppWidgetState(applicationContext, id) { prefs ->
                    val dataKey = stringPreferencesKey(CurrentReadingBookWidget.WIDGET_DATA_KEY)
                    prefs[dataKey] = jsonData
                }
                CurrentReadingBookWidget().update(applicationContext,id)
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("Widget","CurrentReadingBookWidgetWorker.doWork()",e)
        }
        return Result.failure()
    }

}

private fun scheduleImageDownload(imageUrl: String, context: Context) {
    val imageDownloadWork = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
        .setInputData(workDataOf(ImageDownloadWorker.Companion.KEY_IMAGE_URL to imageUrl))
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()
    
    WorkManager.getInstance(context)
        .enqueueUniqueWork(
            "image_download_${imageUrl.hashCode()}",
            ExistingWorkPolicy.REPLACE,
            imageDownloadWork
        )
}