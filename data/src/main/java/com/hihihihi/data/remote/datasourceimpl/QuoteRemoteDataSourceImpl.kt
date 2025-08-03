package com.hihihihi.data.remote.datasourceimpl

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.mapper.toMap
import com.hihihihi.data.remote.datasource.QuoteRemoteDataSource
import com.hihihihi.data.remote.dto.QuoteDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuoteRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : QuoteRemoteDataSource {
    override suspend fun addQuote(quoteDto: QuoteDto): Result<Unit> = try {
        val documentReference = firestore.collection("quotes").document()
        quoteDto.quoteId = documentReference.id

        documentReference.set(quoteDto.toMap()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}