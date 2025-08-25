package com.hihihihi.gureumpage.widgets.common

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hihihihi.gureumpage.widgets.heatmap.HistoryHeatMapWorker
import com.hihihihi.gureumpage.widgets.reading.CurrentReadingBookWidgetWorker
import com.hihihihi.gureumpage.widgets.reading.CurrentReadingBooksWidgetWorker
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdateDispatcher @Inject constructor(
    @ApplicationContext private val context: Context
){
    fun updateCurrentReadingBook() {
        val req = OneTimeWorkRequestBuilder<CurrentReadingBookWidgetWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            CurrentReadingBookWidgetWorker.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            req
        )
    }

    fun updateCurrentReadingBooks() {
        val req = OneTimeWorkRequestBuilder<CurrentReadingBooksWidgetWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            CurrentReadingBooksWidgetWorker.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            req
        )
    }

    fun updateHistoryHeatMap() {
        val req = OneTimeWorkRequestBuilder<HistoryHeatMapWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            HistoryHeatMapWorker.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            req
        )
    }

    fun updateAllWidgets() {
        val w = WorkManager.getInstance(context)

        val currentReadingBookReq  = OneTimeWorkRequestBuilder<CurrentReadingBookWidgetWorker>().build()
        val currentReadingBooksReq = OneTimeWorkRequestBuilder<CurrentReadingBooksWidgetWorker>().build()
        val historyHeatMapReq      = OneTimeWorkRequestBuilder<HistoryHeatMapWorker>().build()

        w.beginUniqueWork(
            UNIQUE_ALL_WIDGETS_WORK,
            ExistingWorkPolicy.REPLACE,
            listOf(currentReadingBookReq, currentReadingBooksReq, historyHeatMapReq)
        ).enqueue()
    }

    companion object {
        private const val UNIQUE_ALL_WIDGETS_WORK = "AllWidgetsUpdate"
        fun getDispatcher(context: Context): WidgetUpdateDispatcher {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                WidgetUpdateEntryPoint::class.java
            )
            return entryPoint.widgetUpdateDispatcher()
        }
    }

}