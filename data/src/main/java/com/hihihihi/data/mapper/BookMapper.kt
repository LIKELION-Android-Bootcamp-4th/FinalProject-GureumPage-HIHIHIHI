package com.hihihihi.data.mapper

import com.hihihihi.data.remote.dto.BookDto
import com.hihihihi.domain.model.Book

fun Book.toDto(): BookDto = BookDto(
    title = title,
    author = author,
    publisher = publisher,
    isbn10 = isbn10,
    isbn13 = isbn13,
    description = description,
    genreIds = genreIds,
    imageUrl = imageUrl,
    totalPage = totalPage
)

fun BookDto.toDomain(): Book = Book(
    title = title,
    author = author,
    publisher = publisher,
    isbn10 = isbn10,
    isbn13 = isbn13,
    description = description,
    genreIds = genreIds,
    imageUrl = imageUrl,
    totalPage = totalPage
)
