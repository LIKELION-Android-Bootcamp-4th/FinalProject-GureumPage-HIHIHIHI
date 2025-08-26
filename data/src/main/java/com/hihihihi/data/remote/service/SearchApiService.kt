package com.hihihihi.data.remote.service

import com.hihihihi.data.remote.dto.BookDetailDto
import com.hihihihi.data.remote.dto.SearchBookListDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    @GET("ItemSearch.aspx")
    suspend fun searchBooks(
        @Query("TTBKey") ttbkey: String,
        @Query("Query") query: String,
        @Query("Start") start: Int,
        @Query("MaxResults") maxResults: Int,
        @Query("SearchTarget") searchTarget: String = "Book",
        @Query("QueryType") queryType: String = "Keyword",
        @Query("Sort") sort: String = "Accuracy",
        @Query("Output") output: String = "JS",
        @Query("Cover") cover: String = "Big",
        @Query("Version") version: String = "20131101"
    ): Response<SearchBookListDto>

    @GET("ItemLookUp.aspx")
    suspend fun getBookDetail(
        @Query("TTBKey") ttbkey: String,
        @Query("itemIdType") itemIdType: String = "ISBN13",
        @Query("ItemId") itemId: String,
        @Query("Output") output: String = "JS",
        @Query("Version") version: String = "20131101"
    ): Response<BookDetailDto>
} 