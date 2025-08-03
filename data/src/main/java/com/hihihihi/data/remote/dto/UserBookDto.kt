package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.hihihihi.data.common.util.toLocalDateTime
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook

@Keep  // Proguard 등 난독화 도구에서 제거되지 않도록 유지
data class UserBookDto(
    // Firestore 문서 ID는 여기엔 기본 포함되지 않으므로
    // 나중에 수동으로 넣어줘야 해서@PropertyName 필요 없음
    var userBookId: String = "", // Firestore 문서 ID를 저장하는 필드

    // Firestore 필드명과 Kotlin 프로퍼티명을 매핑
    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = "",

    @get:PropertyName("book_id") @set:PropertyName("book_id")
    var bookId: String = "",

    @get:PropertyName("title") @set:PropertyName("title")
    var title: String = "",

    @get:PropertyName("author") @set:PropertyName("author")
    var author: String = "",

    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String = "",

    @get:PropertyName("current_page") @set:PropertyName("current_page")
    var currentPage: Int? = 0,

    @get:PropertyName("start_date") @set:PropertyName("start_date")
    var startDate: Timestamp? = null,

    @get:PropertyName("end_date") @set:PropertyName("end_date")
    var endDate: Timestamp? = null,

    @get:PropertyName("total_read_time") @set:PropertyName("total_read_time")
    var totalReadTime: Int? = 0,

    @get:PropertyName("status") @set:PropertyName("status")
    var status: String = "planned", // "planned", "reading", "finished"

    @get:PropertyName("review") @set:PropertyName("review")
    var review: String? = null,

    @get:PropertyName("rating") @set:PropertyName("rating")
    var rating: Double? = null,

    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Timestamp? = null
)