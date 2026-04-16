package com.hihihihi.gureumpage.ui.home.mock

import com.hihihihi.domain.model.Quote
import com.hihihihi.domain.model.ReadingStatus
import com.hihihihi.domain.model.User
import com.hihihihi.domain.model.UserBook
import java.time.LocalDateTime

val mockUser = User("", "히히히히", "새벽독서가", 0, "", LocalDateTime.now())

val mockUserBooks = listOf(
    UserBook(
        userBookId = "ub001",
        userId = "user123",
        title = "죄와 벌",
        author = "표도르 도스토예프스키",
        imageUrl = "https://minumsa.minumsa.com/wp-content/uploads/bookcover/284-죄와벌1_표1-500x851.jpg",
        totalPage = 525,
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
        title = "어린 왕자",
        author = "앙투안 드 생텍쥐페리",
        imageUrl = "https://images-ext-1.discordapp.net/external/Dj-FzWSUevc1s_rJdjgHKEDl8VOS4AU-u3-B94EWA9A/https/contents.kyobobook.co.kr/sih/fit-in/458x0/pdt/9791158511982.jpg?format=webp&width=916&height=1372",
        totalPage = 132,
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

val dummyQuotes = listOf(
    Quote(
        id = "q1",
        userId = "user_123",
        userBookId = "ub1",
        content = "진정한 용기는 두려움을 이겨내는 것이다.",
        pageNumber = 45,
        isLiked = false,
        createdAt = LocalDateTime.now().minusDays(2),
        title = "용기의 심리학",
        author = "브레네 브라운",
        publisher = "마음출판사",
        imageUrl = "https://example.com/image1.jpg"
    ),
    Quote(
        id = "q2",
        userId = "user_123",
        userBookId = "ub1",
        content = "꾸준함이 재능을 이긴다.",
        pageNumber = 88,
        isLiked = true,
        createdAt = LocalDateTime.now().minusDays(1),
        title = "습관의 힘",
        author = "찰스 두히그",
        publisher = "갤럭시북스",
        imageUrl = "https://example.com/image2.jpg"
    ),
    Quote(
        id = "q3",
        userId = "user_123",
        userBookId = "ub2",
        content = "가장 어두운 밤도 끝나고 해는 뜬다.",
        pageNumber = 123,
        isLiked = false,
        createdAt = LocalDateTime.now().minusHours(5),
        title = "레미제라블",
        author = "빅토르 위고",
        publisher = "고전문학사",
        imageUrl = "https://example.com/image3.jpg"
    ),
    Quote(
        id = "q4",
        userId = "user_123",
        userBookId = "ub3",
        content = "행복은 결과가 아니라 과정이다.",
        pageNumber = 12,
        isLiked = true,
        createdAt = LocalDateTime.now().minusDays(3),
        title = "행복의 기원",
        author = "서은국",
        publisher = "21세기북스",
        imageUrl = "https://example.com/image4.jpg"
    ),
    Quote(
        id = "q5",
        userId = "user_123",
        userBookId = "ub4",
        content = "생각은 말로, 말은 행동으로 이어진다.",
        pageNumber = null,
        isLiked = false,
        createdAt = LocalDateTime.now(),
        title = "생각의 지도",
        author = "리처드 니스벳",
        publisher = "열린책들",
        imageUrl = "https://example.com/image5.jpg"
    )
)
