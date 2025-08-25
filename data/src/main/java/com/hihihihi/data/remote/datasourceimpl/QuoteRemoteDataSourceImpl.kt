package com.hihihihi.data.remote.datasourceimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.mapper.toMap
import com.hihihihi.data.remote.datasource.QuoteRemoteDataSource
import com.hihihihi.data.remote.dto.QuoteDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuoteRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : QuoteRemoteDataSource {
    // 새로운 명언 데이터를 Firestore에 추가하는 suspend 함수
    override suspend fun addQuote(quoteDto: QuoteDto): Result<Unit> = try {
        // quotes 컬렉션에 새 문서 생성 (자동 ID 생성)
        val documentReference = firestore.collection("quotes").document()

        // DTO를 Map 형태로 변환 후 Firestore에 저장, await()로 완료 대기
        documentReference.set(quoteDto.toMap()).await()

        // 성공 결과 반환
        Result.success(Unit)
    } catch (e: Exception) {
        // 에러 발생 시 실패 결과 반환
        Result.failure(e)
    }

    override fun getQuotes(userId: String): Flow<List<QuoteDto>> = callbackFlow {
        val quotesCollection = firestore.collection("quotes")
            .whereEqualTo("user_id", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val quotes = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(QuoteDto::class.java)?.apply {
                        quoteId = document.id
                    }
                } ?: emptyList()
                trySend(quotes)
            }
        awaitClose { quotesCollection.remove() }
    }

    override fun getQuotesByUserBookId(userBookId: String): Flow<List<QuoteDto>> = callbackFlow {
        val quotesCollection = firestore.collection("quotes")
            .whereEqualTo("userbook_id", userBookId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val quotes = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(QuoteDto::class.java)?.apply {
                        quoteId = document.id
                    }
                } ?: emptyList()
                trySend(quotes)
            }
        awaitClose { quotesCollection.remove() }
    }

    override suspend fun deleteQuote(quoteId: String): Result<Unit> = try{
        firestore.collection("quotes")
            .document(quoteId)
            .delete()
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateQuote(
        quoteId: String,
        content: String,
        pageNumber: Int?
    ): Result<Unit> = try {
        val updates = mutableMapOf<String, Any?>(
            "content" to content,
            "page_number" to pageNumber
        ).filterValues { it != null }

        firestore.collection("quotes")
            .document(quoteId)
            .update(updates)
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
