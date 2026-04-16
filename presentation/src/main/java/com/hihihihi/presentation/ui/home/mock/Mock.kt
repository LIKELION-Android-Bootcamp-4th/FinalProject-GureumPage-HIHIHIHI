package com.hihihihi.presentation.ui.home.mock

import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.presentation.ui.model.HomeUiModel
import com.hihihihi.presentation.ui.model.QuoteUiModel
import com.hihihihi.presentation.ui.model.UserBookUiModel
import java.time.LocalDateTime

val mockUserBooks = listOf(
    UserBookUiModel(
        userBookId = "ub001",
        title = "죄와 벌",
        author = "표도르 도스토예프스키",
        imageUrl = "https://minumsa.minumsa.com/wp-content/uploads/bookcover/284-죄와벌1_표1-500x851.jpg",
        status = ReadingStatus.READING,
        totalPage = 525,
        currentPage = 170,
        startDate = LocalDateTime.of(2025, 7, 29, 8, 0),
        endDate = null,
        totalReadTime = 5213,
        review = null,
        rating = null,
        category = null,
        publisher = null,
        isbn13 = null,
        description = null,
    ),
    UserBookUiModel(
        userBookId = "ub002",
        title = "어린 왕자",
        author = "앙투안 드 생텍쥐페리",
        imageUrl = "https://contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791158511982.jpg",
        status = ReadingStatus.READING,
        totalPage = 132,
        currentPage = 98,
        startDate = LocalDateTime.of(2025, 7, 25, 9, 0),
        endDate = LocalDateTime.of(2025, 7, 29, 10, 0),
        totalReadTime = 18000,
        review = "짧지만 깊은 울림이 있는 책",
        rating = 4.5,
        category = null,
        publisher = null,
        isbn13 = null,
        description = null,
    ),
)

val dummyQuotes = listOf(
    QuoteUiModel(
        id = "q1",
        content = "진정한 용기는 두려움을 이겨내는 것이다.",
        pageNumber = 45,
        isLiked = false,
        createdAt = LocalDateTime.now().minusDays(2),
        title = "용기의 심리학",
        author = "브레네 브라운",
        publisher = "마음출판사",
        imageUrl = "https://example.com/image1.jpg",
    ),
    QuoteUiModel(
        id = "q2",
        content = "꾸준함이 재능을 이긴다.",
        pageNumber = 88,
        isLiked = true,
        createdAt = LocalDateTime.now().minusDays(1),
        title = "습관의 힘",
        author = "찰스 두히그",
        publisher = "갤럭시북스",
        imageUrl = "https://example.com/image2.jpg",
    ),
)

val mockHomeUiModel = HomeUiModel(
    nickname = "히히히히",
    appellation = "새벽독서가",
    dailyGoalTime = 3600,
    userBooks = mockUserBooks,
    quotes = dummyQuotes,
    todayReadTime = 1800,
)
