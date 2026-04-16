package com.hihihihi.presentation.ui.bookdetail.mock

import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.RecordType
import com.hihihihi.presentation.ui.model.HistoryUiModel
import com.hihihihi.presentation.ui.model.QuoteUiModel
import com.hihihihi.presentation.ui.model.UserBookUiModel
import java.time.LocalDateTime

val now = LocalDateTime.now()

val dummyRecords = listOf(
    HistoryUiModel(
        readTime = 19 * 60 + 30,
        readPageCount = 10,
        date = now,
        startTime = now.minusMinutes(20),
        endTime = now,
        recordType = RecordType.TIMER,
    ),
    HistoryUiModel(
        readTime = 5 * 60,
        readPageCount = 3,
        date = now,
        startTime = now.minusMinutes(10),
        endTime = now,
        recordType = RecordType.MANUAL,
    ),
    HistoryUiModel(
        readTime = 15 * 60,
        readPageCount = 8,
        date = now.minusDays(1),
        startTime = now.minusDays(1).minusMinutes(30),
        endTime = now.minusDays(1),
        recordType = RecordType.TIMER,
    ),
)

val dummyQuotes = listOf(
    QuoteUiModel(
        id = "1",
        content = "네가 4시에 온다면 난 3시부터 행복할거야. 네가 4시에 온다면 난 3시부터 행복할거야.",
        pageNumber = 120,
        isLiked = false,
        createdAt = LocalDateTime.of(2025, 7, 29, 10, 0),
        title = "어린 왕자",
        author = "생텍쥐페리",
        publisher = "출판사A",
        imageUrl = "",
    ),
    QuoteUiModel(
        id = "2",
        content = "사막이 아름다운 건 어딘가에 샘을 감추고 있기 때문이야.",
        pageNumber = null,
        isLiked = true,
        createdAt = LocalDateTime.of(2025, 7, 30, 10, 0),
        title = "어린 왕자",
        author = "생텍쥐페리",
        publisher = "출판사A",
        imageUrl = "",
    ),
)

val dummyUserBook = UserBookUiModel(
    userBookId = "dummyId123",
    title = "더미 책 제목",
    author = "더미 작가",
    imageUrl = "https://dummyimage.com/200x300/cccccc/000000&text=Book+Cover",
    status = ReadingStatus.READING,
    totalPage = 350,
    currentPage = 120,
    startDate = LocalDateTime.of(2023, 1, 10, 0, 0),
    endDate = null,
    totalReadTime = 3600,
    review = "아직 읽는 중이지만 흥미로워요.",
    rating = 4.5,
    category = "소설",
    publisher = null,
    isbn13 = "9781234567897",
    description = null,
)
