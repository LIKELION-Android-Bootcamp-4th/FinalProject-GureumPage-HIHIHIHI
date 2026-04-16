package com.hihihihi.gureumpage.ui.search.model

import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.SearchBook
import java.time.LocalDateTime

data class Book(
    val searchBook: SearchBook,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val currentPage: Int,
    val totalPage: Int,
    val status: ReadingStatus,
)
