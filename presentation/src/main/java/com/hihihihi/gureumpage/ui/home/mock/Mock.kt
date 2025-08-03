package com.hihihihi.gureumpage.ui.home.mock

import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.UserBook
import java.time.LocalDateTime

data class User(
    val nickName: String,
    val appellation: String
)



val mockUser = User("히히히히", "새벽독서가")


val mockUserBooks = listOf(
    UserBook(
        userBookId = "ub001",
        userId = "user123",
        bookId = "book001",
        title = "죄와 벌",
        author = "표도르 도스토예프스키",
        imageUrl = "https://example.com/covers/crime_and_punishment.jpg",
        currentPage = 170,
        startDate = LocalDateTime.of(2025, 7, 29, 8, 0),
        endDate = null,
        totalReadTime = 5213, // 분 단위
        status = ReadingStatus.READING,
        review = null,
        rating = null,
        createdAt = LocalDateTime.of(2025, 7, 29, 7, 30)
    ),
    UserBook(
        userBookId = "ub002",
        userId = "user123",
        bookId = "book002",
        title = "어린 왕자",
        author = "앙투안 드 생텍쥐페리",
        imageUrl = "https://example.com/covers/little_prince.jpg",
        currentPage = 98,
        startDate = LocalDateTime.of(2025, 7, 25, 9, 0),
        endDate = LocalDateTime.of(2025, 7, 29, 10, 0),
        totalReadTime = 521312423,
        status = ReadingStatus.READING,
        review = "짧지만 깊은 울림이 있는 책",
        rating = 4.5,
        createdAt = LocalDateTime.of(2025, 7, 25, 8, 50)
    )
)
