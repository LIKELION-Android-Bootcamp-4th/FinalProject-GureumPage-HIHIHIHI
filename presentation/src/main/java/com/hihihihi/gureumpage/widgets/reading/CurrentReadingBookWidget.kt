package com.hihihihi.gureumpage.widgets.reading

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.google.gson.Gson
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.widgets.common.WidgetBook
import com.hihihihi.gureumpage.widgets.common.WidgetParams
import com.hihihihi.gureumpage.widgets.common.actions.OpenAddQuoteAction
import com.hihihihi.gureumpage.widgets.common.actions.OpenBookDetailAction
import com.hihihihi.gureumpage.widgets.common.actions.OpenMissedRecordAction
import com.hihihihi.gureumpage.widgets.common.actions.OpenTimerAction
import com.hihihihi.gureumpage.widgets.components.ActionIcon

import androidx.glance.unit.ColorProvider
import androidx.glance.background
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.wrapContentHeight
import androidx.glance.state.PreferencesGlanceStateDefinition

import java.io.File

class CurrentReadingBookWidget : GlanceAppWidget()  {
    override val stateDefinition = PreferencesGlanceStateDefinition
    companion object {
        const val WIDGET_DATA_KEY = "current_book_widget_data"
    }

    override val sizeMode: SizeMode = SizeMode.Exact
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val state = currentState<Preferences>()

            val jsonData = state[stringPreferencesKey(WIDGET_DATA_KEY)].orEmpty()

            // JSON String을 WidgetData로 파싱
            val widgetData = try {
                Gson().fromJson(jsonData, WidgetBook::class.java) ?: WidgetBook()
            } catch (e: Exception) {
                WidgetBook()
            }

            Row(
                modifier = GlanceModifier
//                    .fillMaxSize()
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(ColorProvider(R.color.background))
                    .padding(12.dp)
                    .clickable(
                        onClick = actionRunCallback<OpenBookDetailAction>(
                            parameters = actionParametersOf(WidgetParams.BookId to widgetData.bookId)
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val imageProvider = if (widgetData.imageUrl.isNotBlank()) {
                    try {
                        val cacheDir = context.cacheDir
                        val imageDir = File(cacheDir, "widget_images")
                        val fileName = widgetData.imageUrl.hashCode().toString() + ".png"
                        val imageFile = File(imageDir, fileName)
                        
                        if (imageFile.exists()) {
                            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                            if (bitmap != null) {
                                ImageProvider(bitmap)
                            } else {
                                ImageProvider(R.drawable.ic_book_outline)
                            }
                        } else {
                            ImageProvider(R.drawable.ic_book_outline)
                        }
                    } catch (e: Exception) {
                        ImageProvider(R.drawable.ic_book_outline)
                    }
                } else {
                    ImageProvider(R.drawable.ic_book_outline)
                }

                Image(
                    provider = imageProvider,
                    contentDescription = "책 표지",
                    modifier = GlanceModifier.size(48.dp)
                )

                Spacer(GlanceModifier.width(12.dp))

                Column(modifier = GlanceModifier.defaultWeight()) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.Start) {
                            Text(
                                text = widgetData.title.ifBlank { "읽는 중인 책 제목" },
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1
                            )
                        }
                        Row(modifier = GlanceModifier
                            .defaultWeight(),
                            verticalAlignment = Alignment.CenterVertically) {

                            // 놓친 기록
                            Column(
                                modifier = GlanceModifier.defaultWeight(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ActionIcon(
                                    resId = R.drawable.ic_edit_alt_outline,
                                    description = "놓친 기록",
                                    action = actionRunCallback<OpenMissedRecordAction>(
                                        parameters = actionParametersOf(WidgetParams.BookId to widgetData.bookId)
                                    )
                                )
                            }


                            // 타이머
                            Column(
                                modifier = GlanceModifier.defaultWeight(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ActionIcon(
                                    resId = R.drawable.ic_alarm_outline,
                                    description = "놓친 기록",
                                    action = actionRunCallback<OpenTimerAction>(
                                        parameters = actionParametersOf(WidgetParams.BookId to widgetData.bookId)
                                    )
                                )
                            }

                            // 필사 추가
                            Column(
                                modifier = GlanceModifier.defaultWeight(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ActionIcon(
                                    resId = R.drawable.ic_lightbulb_outline,
                                    description = "필사 추가",
                                    action = actionRunCallback<OpenAddQuoteAction>(
                                        parameters = actionParametersOf(WidgetParams.BookId to widgetData.bookId)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}