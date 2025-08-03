package com.hihihihi.data.mapper

import com.hihihihi.data.remote.dto.GenreDto
import com.hihihihi.domain.model.Genre

fun GenreDto.toDomain(): Genre = Genre(
    genreId = genreId,
    genreName = genreName
)

fun Genre.toDto(): GenreDto = GenreDto(
    genreId = genreId,
    genreName = genreName
)
