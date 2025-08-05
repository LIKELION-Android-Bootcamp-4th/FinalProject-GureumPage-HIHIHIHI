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
        // 자동 생성된 문서 ID를 DTO에 할당
        quoteDto.quoteId = documentReference.id

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
}
