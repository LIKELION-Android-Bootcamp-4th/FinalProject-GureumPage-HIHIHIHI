package com.hihihihi.data.remote.dto

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.hihihihi.data.common.util.toLocalDateTime
import com.hihihihi.domain.model.History

@Keep
data class HistoryDto(
    var historyId: String = "",

    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = "",

    @get:PropertyName("userbook_id") @set:PropertyName("userbook_id")
    var userBookId: String = "",

    @get:PropertyName("date") @set:PropertyName("date")
    var date: Timestamp? = null,

    @get:PropertyName("start_time") @set:PropertyName("start_time")
    var startTime: Timestamp? = null,

    @get:PropertyName("end_time") @set:PropertyName("end_time")
    var endTime: Timestamp? = null,

    @get:PropertyName("read_time") @set:PropertyName("read_time")
    var readTime: Int = 0,

    @get:PropertyName("read_page_count") @set:PropertyName("read_page_count")
    var readPageCount: Int = 0,

    @get:PropertyName("record_type") @set:PropertyName("record_type")
    var recordType: String = "timer" // "timer", "manual"
)
