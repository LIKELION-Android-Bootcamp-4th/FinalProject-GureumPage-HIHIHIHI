package com.hihihihi.data.mapper

import com.hihihihi.data.remote.dto.SearchBookDto
import com.hihihihi.domain.model.SearchBook

fun SearchBookDto.toDomain(): SearchBook = SearchBook(
    title = title ?: "",
    author = author ?: "",
    publisher = publisher ?: "",
    isbn = isbn ?: "",
    description = description ?: "",
    coverImageUrl = cover ?: "",
    categoryName = categoryName ?: "",
)
