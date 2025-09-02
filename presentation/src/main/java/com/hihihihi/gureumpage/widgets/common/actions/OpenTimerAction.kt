package com.hihihihi.gureumpage.widgets.common.actions

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

import androidx.core.net.toUri
import com.hihihihi.gureumpage.widgets.common.DeepLinks
import com.hihihihi.gureumpage.widgets.common.WidgetParams

class OpenTimerAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val bookId = parameters[WidgetParams.BookId].orEmpty()
        if(bookId.isNotEmpty()) {
            val deepLink = DeepLinks.createTimerLink(bookId)
            val intent = Intent(Intent.ACTION_VIEW, deepLink.toUri())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            context.startActivity(intent)
        }
    }
}