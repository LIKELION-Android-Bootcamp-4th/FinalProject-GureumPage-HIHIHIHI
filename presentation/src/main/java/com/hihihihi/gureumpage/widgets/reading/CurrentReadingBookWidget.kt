package com.hihihihi.gureumpage.widgets.reading

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
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

import androidx.glance.unit.ColorProvider
import androidx.glance.background
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.wrapContentHeight
import androidx.glance.state.PreferencesGlanceStateDefinition

import java.io.File

class CurrentReadingBookWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition
    override val sizeMode: SizeMode = SizeMode.Exact

    companion object {
        const val WIDGET_DATA_KEY = "current_book_widget_data"
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state = currentState<Preferences>()
            val jsonData = state[stringPreferencesKey(WIDGET_DATA_KEY)].orEmpty()
            val widgetData = try {
                Gson().fromJson(jsonData, WidgetBook::class.java) ?: WidgetBook()
            } catch (_: Exception) {
                WidgetBook()
            }

            val cardBg = ColorProvider(R.color.background)      // 어두운 카드
            val textFg = ColorProvider(R.color.gray700)         // 밝은 텍스트
            val iconTint = ColorProvider(R.color.primary)       // 노란 아이콘

            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(cardBg)
                    .cornerRadius(20.dp)
                    .padding(16.dp)
                    .padding(vertical = 6.dp)
                    .clickable(
                        onClick = actionRunCallback<OpenBookDetailAction>(
                            parameters = actionParametersOf(WidgetParams.BookId to widgetData.bookId)
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start
            ) {
                // 표지
                Image(
                    provider = loadWidgetImageOrFallback(context, widgetData.imageUrl),
                    contentDescription = "책 표지",
                    modifier = GlanceModifier.size(52.dp).cornerRadius(6.dp)
                )

                Spacer(GlanceModifier.width(12.dp))

                // 제목 + 우측 액션들
                Row(
                    modifier = GlanceModifier.defaultWeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 제목
                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = widgetData.title.ifBlank { "읽는 중인 책 제목" },
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = textFg
                            ),
                            maxLines = 1
                        )
                    }

                    // 우측 아이콘 3개 (개별 클릭)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.padding(start = 8.dp)
                    ) {
                        // 놓친 기록
                        Image(
                            provider = ImageProvider(R.drawable.ic_edit_alt_filled),
                            contentDescription = "놓친 기록",
                            modifier = GlanceModifier
                                .size(32.dp)
                                .clickable(
                                    onClick = actionRunCallback<OpenMissedRecordAction>(
                                        parameters = actionParametersOf(WidgetParams.BookId to widgetData.bookId)
                                    )
                                ),
                            colorFilter = ColorFilter.tint(iconTint)
                        )

                        Spacer(GlanceModifier.width(12.dp))

                        // 타이머
                        Image(
                            provider = ImageProvider(R.drawable.ic_alarm_filled),
                            contentDescription = "타이머",
                            modifier = GlanceModifier
                                .size(32.dp)
                                .clickable(
                                    onClick = actionRunCallback<OpenTimerAction>(
                                        parameters = actionParametersOf(WidgetParams.BookId to widgetData.bookId)
                                    )
                                ),
                            colorFilter = ColorFilter.tint(iconTint)
                        )

                        Spacer(GlanceModifier.width(12.dp))

                        // 필사 추가
                        Image(
                            provider = ImageProvider(R.drawable.ic_lightbulb_filled),
                            contentDescription = "필사 추가",
                            modifier = GlanceModifier
                                .size(32.dp)
                                .clickable(
                                    onClick = actionRunCallback<OpenAddQuoteAction>(
                                        parameters = actionParametersOf(WidgetParams.BookId to widgetData.bookId)
                                    )
                                ),
                            colorFilter = ColorFilter.tint(iconTint)
                        )
                    }
                }
            }
        }
    }
}

private fun loadWidgetImageOrFallback(context: Context, url: String?): ImageProvider {
    return if (!url.isNullOrBlank()) {
        try {
            val imageDir = File(context.cacheDir, "widget_images")
            val fileName = url.hashCode().toString() + ".png"
            val imageFile = File(imageDir, fileName)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                if (bitmap != null) ImageProvider(bitmap)
                else ImageProvider(R.drawable.ic_holder_book)
            } else {
                ImageProvider(R.drawable.ic_holder_book)
            }
        } catch (_: Exception) {
            ImageProvider(R.drawable.ic_holder_book)
        }
    } else {
        ImageProvider(R.drawable.ic_holder_book)
    }
}