package com.hihihihi.gureumpage.widgets.reading

import android.content.Context
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
class CurrentReadingBooksWidgetWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getUserBooksByStatus: GetUserBooksByStatusUseCase
) : CoroutineWorker(appContext, params) {

    companion object {
        const val UNIQUE_WORK_NAME = "CurrentReadingBooksWokrer"
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUid: String
        get() = auth.currentUser!!.uid

    override suspend fun doWork(): Result {

        try {
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = manager.getGlanceIds(CurrentReadingBooksWidget::class.java)

            val books = getUserBooksByStatus(currentUid, ReadingStatus.READING).first()
            val imageUrls = books.mapNotNull { it.imageUrl }.filter { it.isNotBlank() }

            scheduleImageDownloads(imageUrls, applicationContext)

            glanceIds.forEach { id ->
                val widgetBooks = books.map { book ->
                    WidgetBook(
                        bookId = book.userBookId,
                        title = book.title,
                        author = book.author.orEmpty(),
                        imageUrl = book.imageUrl.orEmpty()
                    )
                }

                val jsonData = Gson().toJson(widgetBooks)
                updateAppWidgetState(applicationContext, id) { prefs ->
                    val dataKey = stringPreferencesKey(CurrentReadingBooksWidget.WIDGET_DATA_KEY)

                    prefs[dataKey] = jsonData
                }
                CurrentReadingBooksWidget().update(applicationContext,id)
            }
            return Result.success()
        } catch (e: Exception) { }
        return Result.failure()
    }
}

private fun scheduleImageDownloads(urls: List<String>, context: Context) {
    val distinct = urls.filter { it.isNotBlank() }.distinct().take(4)
    val wm = WorkManager.getInstance(context)

    distinct.forEach { url ->
        val work = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setInputData(workDataOf(ImageDownloadWorker.KEY_IMAGE_URL to url))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        // URL 해시를 이름으로 하여 중복 다운로드 방지
        wm.enqueueUniqueWork(
            "image_download_${url.hashCode()}",
            ExistingWorkPolicy.KEEP, // 이미 진행/완료된 작업이면 재실행 안 함
            work
        )
    }
}