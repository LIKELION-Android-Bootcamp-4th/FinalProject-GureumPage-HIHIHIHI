package com.hihihihi.gureumpage.ui.bookdetail.mock

import com.hihihihi.domain.model.History
import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.RecordType
import com.hihihihi.domain.model.UserBook
import java.time.LocalDateTime

val now = LocalDateTime.now()
val dummyRecords = listOf(
    History(
        id = "1",
        userId = "user1",
        userBookId = "book1",
        date = now,
        startTime = now.minusMinutes(20),
        endTime = now,
        readTime = 19 * 60 + 30,
        readPageCount = 10,
        recordType = RecordType.TIMER
    ),
    History(
        id = "2",
        userId = "user1",
        userBookId = "book1",
        date = now,
        startTime = now.minusMinutes(10),
        endTime = now,
        readTime = 5 * 60,
        readPageCount = 3,
        recordType = RecordType.MANUAL
    ),
    History(
        id = "3",
        userId = "user1",
        userBookId = "book1",
        date = now.minusDays(1),
        startTime = now.minusDays(1).minusMinutes(30),
        endTime = now.minusDays(1),
        readTime = 15 * 60,
        readPageCount = 8,
        recordType = RecordType.TIMER
    )
)

val dummyQuotes = listOf(
    Quote(
        id = "1",
        userId = "user1",
        userBookId = "book1",
        content = "네가 4시에 온다면 난 3시부터 행복할거야. 네가 4시에 온다면 난 3시부터 행복할거야.네가 4시에 온다면 난 3시부터 행복할거야.네가 4시에 온다면 난 3시부터 행복할거야.네가 4시에 온다면 난 3시부터 행복할거야.네가 4시에 온다면 난 3시부터 행복할거야.네가 4시에 온다면 난 3시부터 행복할거야.네가 4시에 온다면 난 3시부터 행복할거야.네가 4시에 온다면 난 3시부터 행복할거야.",
        pageNumber = 120,
        isLiked = false,
        createdAt = LocalDateTime.of(2025, 7, 29, 10, 0),
        title = "어린 왕자",
        author = "생텍쥐페리",
        publisher = "출판사A",
        imageUrl = ""
    ),
    Quote(
        id = "2",
        userId = "user1",
        userBookId = "book1",
        content = "사막이 아름다운 건 어딘가에 샘을 감추고 있기 때문이야.",
        pageNumber = null,
        isLiked = true,
        createdAt = LocalDateTime.of(2025, 7, 30, 10, 0),
        title = "어린 왕자",
        author = "생텍쥐페리",
        publisher = "출판사A",
        imageUrl = ""
    )
)

val dummyUserBook = UserBook(
    userBookId = "dummyId123",
    userId = "userId456",
    isbn10 = "1234567890",
    isbn13 = "9781234567897",
    title = "더미 책 제목",
    author = "더미 작가",
    imageUrl = "https://dummyimage.com/200x300/cccccc/000000&text=Book+Cover",
    isLiked = true,
    totalPage = 350,
    currentPage = 120,
    startDate = LocalDateTime.of(2023, 1, 10, 0, 0),
    endDate = null,
    totalReadTime = 3600, // 초 단위
    status = ReadingStatus.READING,
    review = "아직 읽는 중이지만 흥미로워요.",
    rating = 4.5,
    category = "소설",
    createdAt = LocalDateTime.now()
)
