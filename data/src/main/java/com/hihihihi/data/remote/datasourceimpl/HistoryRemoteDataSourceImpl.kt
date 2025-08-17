package com.hihihihi.data.remote.datasourceimpl

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.remote.datasource.HistoryRemoteDataSource
import com.hihihihi.data.remote.dto.DailyReadPageDto
import com.hihihihi.data.remote.dto.HistoryDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

class HistoryRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HistoryRemoteDataSource {
    override fun getHistoriesByUserBookId(userBookId: String): Flow<List<HistoryDto>> = callbackFlow {
        val historiesCollection = firestore.collection("histories")
            .whereEqualTo("userbook_id", userBookId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

//                val histories = snapshot?.documents?.mapNotNull { document ->
//                    document.toObject(HistoryDto::class.java)?.apply {
//                        historyId = document.id
//                    }
//                } ?: emptyList()

                val histories = snapshot?.documents
                    ?.mapNotNull { it.toHistoryDtoSafely() } // 🔐 안전 매핑
                    ?: emptyList()
                trySend(histories)
            }
        awaitClose { historiesCollection.remove() }
    }

    override fun getHistoriesByUserId(userId: String): Flow<List<HistoryDto>> = callbackFlow {
        val historiesCollection = firestore.collection("histories")
            .whereEqualTo("user_id", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val histories = snapshot?.documents
                    ?.mapNotNull { it.toHistoryDtoSafely() }
                    ?: emptyList()
                trySend(histories)
            }
        awaitClose { historiesCollection.remove() }
    }

    override fun getTodayHistoriesByUserId(userId: String): Flow<List<HistoryDto>> {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = Timestamp(calendar.time)

        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endOfDay = Timestamp(calendar.time)

        return callbackFlow {
            val listenerRegistration = firestore.collection("histories")
                .whereEqualTo("user_id", userId)
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .whereLessThanOrEqualTo("date", endOfDay)
                .addSnapshotListener { snapshot, error ->
                    if (snapshot != null) {
                        snapshot.documents.forEach {
                            Log.e("Debug", "date field: ${it.get("date")}")
                        }
                    }
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val histories = snapshot?.documents
                        ?.mapNotNull { it.toHistoryDtoSafely() }
                        ?: emptyList()

                    Log.e("TAG", "getTodayHistoriesByUserId: ${histories}", )

                    trySend(histories)
                }

            awaitClose {
                listenerRegistration.remove()
            }
        }
    }


    override suspend fun addHistory(
        historyDto: HistoryDto,
        uid: String
    ): Result<Unit> = try {
        val historyRef = firestore.collection("histories").document()
        historyDto.historyId = historyRef.id
        historyRef.set(historyDto).await()

        val dateStr = historyDto.date?.toDate()?.let { ts ->
            val cal = java.util.Calendar.getInstance()
            cal.time = ts
            "%04d-%02d-%02d".format(
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.DAY_OF_MONTH)
            )
        } ?: throw IllegalArgumentException("date is null")


        val querySnapshot = firestore.collection("daily_read_pages")
            .whereEqualTo("uid", uid)
            .whereEqualTo("date", dateStr)
            .get()
            .await()

        if (querySnapshot.documents.isEmpty()) {
            val newDaily = DailyReadPageDto(
                uid = uid,
                date = dateStr,
                totalReadPageCount = historyDto.readPageCount.toLong(),
                updatedAt = Timestamp.now()
            )
            firestore.collection("daily_read_pages").document().set(newDaily).await()
        } else {
            val doc = querySnapshot.documents.first()
            val currentCount = doc.getLong("totalReadPageCount") ?: 0L
            val updatedCount = currentCount + historyDto.readPageCount
            doc.reference.update(
                mapOf(
                    "totalReadPageCount" to updatedCount,
                    "updated_at" to Timestamp.now()
                )
            ).await()
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun DocumentSnapshot.toHistoryDtoSafely(): HistoryDto? {
        return try {
            fun numToInt(vararg keys: String): Int {
                for (k in keys) {
                    (get(k) as? Number)?.let { return it.toInt() }
                }
                return 0
            }

            HistoryDto(
                historyId = id,
                userId = getString("user_id") ?: "",
                userBookId = getString("userbook_id") ?: "",
                date = getTimestamp("date"),
                startTime = getTimestamp("start_time"),
                endTime = getTimestamp("end_time"),
                // 혼재 키 대응: read_time / readtime
                readTime = numToInt("read_time", "readtime"),
                // 혼재 키 대응: read_page_count / readPageCount
                readPageCount = numToInt("read_page_count", "readPageCount"),
                // 혼재 키 대응: record_type / recode_type (오타 문서 대비)
                recordType = (getString("record_type") ?: getString("recode_type")) ?: "timer"
            )
        } catch (_: Exception) {
            null // 개별 문서 파싱 실패는 무시하고 나머지만 표시
        }
    }
}