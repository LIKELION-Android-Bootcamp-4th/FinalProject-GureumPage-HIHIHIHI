package com.hihihihi.gureumpage.widgets.reading

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.hihihihi.gureumpage.widgets.common.WidgetUpdateDispatcher

class CurrentReadingBooksReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CurrentReadingBooksWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let { WidgetUpdateDispatcher.getDispatcher(it).updateCurrentReadingBooks() }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when(intent.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
            AppWidgetManager.ACTION_APPWIDGET_ENABLED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_USER_UNLOCKED-> {
                WidgetUpdateDispatcher.getDispatcher(context).updateCurrentReadingBooks()
            }
        }
    }
}
