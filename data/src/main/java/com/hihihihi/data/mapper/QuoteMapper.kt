package com.hihihihi.data.mapper

import com.hihihihi.data.common.util.toLocalDateTime
import com.hihihihi.data.remote.dto.QuoteDto
import com.hihihihi.domain.model.Quote
import java.time.LocalDateTime

fun Quote.toDto(): QuoteDto = QuoteDto(
    quoteId = id,
    userId = userId,
    userBookId = userBookId,
    content = content,
    pageNumber = pageNumber,
    isLiked = isLiked,
    createdAt = null //TODO FireStore 에서 규칙 변경해서 자동으로 서버 시간 채워지도록 테스트 해봐야 함
)

fun QuoteDto.toDomain(): Quote = Quote(
    id = quoteId,
    userId = userId,
    userBookId = userBookId,
    content = content,
    pageNumber = pageNumber,
    isLiked = isLiked,
    createdAt = createdAt?.toLocalDateTime()
)
