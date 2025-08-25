package com.hihihihi.gureumpage.widgets.reading

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.dp
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
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.widgets.common.WidgetBook
import com.hihihihi.gureumpage.widgets.common.WidgetParams
import com.hihihihi.gureumpage.widgets.common.actions.OpenBookDetailAction
import java.io.File

class CurrentReadingBooksWidget : GlanceAppWidget()  {

    companion object {
        const val WIDGET_DATA_KEY = "current_books_widget_data"
    }

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val state = currentState<Preferences>()
            val jsonData = state[stringPreferencesKey(WIDGET_DATA_KEY)] ?: ""

            // JSON String을 WidgetData로 파싱
            val books = try {
                val type = object : TypeToken<List<WidgetBook>>() {}.type
                Gson().fromJson<List<WidgetBook>>(jsonData, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            // 최대 4권까지만 표시
            val displayBooks = books.take(4)

            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(12.dp)
                    .background(ColorProvider(R.color.background))
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = GlanceModifier
                        .padding(bottom = 8.dp)
                    // (선택) 헤더 전체 클릭해서 앱/서재 열기
                    // 1) 액티비티 직접 열기 (DeepLink가 있으면 그걸 사용)
                    // .clickable(actionStartActivity(MainActivity::class.java))
                    // 2) 콜백으로 처리
                    // .clickable(actionRunCallback<OpenLibraryAction>())
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_launcher_foreground), // 적절한 앱 아이콘으로 교체
                        contentDescription = "앱 아이콘",
                        modifier = GlanceModifier.size(24.dp)
                    )
                    Spacer(GlanceModifier.width(8.dp))
                    Text(text = "구름한장과 책 읽기")
                }

                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 4칸 고정 슬롯
                    for (slot in 0 until 4) {
                        Column(
                            modifier = GlanceModifier
                                .defaultWeight()          // ← 균등 너비
                                .padding(horizontal = 4.dp), // 칸 간격(선택)
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (slot < displayBooks.size) {
                                val book = displayBooks[slot]
                                val provider = loadWidgetImageOrFallback(context, book.imageUrl)

                                Image(
                                    provider = provider,
                                    contentDescription = book.title,
                                    modifier = GlanceModifier
                                        .size(60.dp)
                                        .clickable(
                                            onClick = actionRunCallback<OpenBookDetailAction>(
                                                parameters = actionParametersOf(
                                                    WidgetParams.BookId to book.bookId
                                                )
                                            )
                                        )
                                )
                            } else {
                                // 비어있는 슬롯: 플레이스홀더(클릭 없음)
                                Image(
                                    provider = ImageProvider(R.drawable.ic_book_outline),
                                    contentDescription = "빈 슬롯",
                                    modifier = GlanceModifier.size(60.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 표지 캐시 로딩 헬퍼: 실패 시 기본 아이콘 반환 */
private fun loadWidgetImageOrFallback(context: Context, url: String?): ImageProvider {
    return if (!url.isNullOrBlank()) {
        try {
            val imageDir = File(context.cacheDir, "widget_images")
            val fileName = url.hashCode().toString() + ".png"
            val imageFile = File(imageDir, fileName)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                if (bitmap != null) ImageProvider(bitmap)
                else ImageProvider(R.drawable.ic_book_outline)
            } else {
                ImageProvider(R.drawable.ic_book_outline)
            }
        } catch (_: Exception) {
            ImageProvider(R.drawable.ic_book_outline)
        }
    } else {
        ImageProvider(R.drawable.ic_book_outline)
    }
}