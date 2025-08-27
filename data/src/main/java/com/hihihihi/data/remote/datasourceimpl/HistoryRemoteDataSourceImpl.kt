package com.hihihihi.data.remote.datasourceimpl

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
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
            .whereEqualTo("user_id", FirebaseAuth.getInstance().currentUser?.uid)
            .whereEqualTo("userbook_id", userBookId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val histories = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(HistoryDto::class.java)?.apply {
                        historyId = document.id
                    }
                } ?: emptyList()
                trySend(histories)
            }
        FirestoreListenerManager.add(historiesCollection)
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
                val histories = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(HistoryDto::class.java)?.apply {
                        historyId = document.id
                    }
                } ?: emptyList()
                trySend(histories)
            }
        FirestoreListenerManager.add(historiesCollection)
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
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val histories = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(HistoryDto::class.java)
                    } ?: emptyList()
                    trySend(histories)
                }

            FirestoreListenerManager.add(listenerRegistration)
            awaitClose {
                listenerRegistration.remove()
            }
        }
    }


    override suspend fun addHistory(
        historyDto: HistoryDto,
        uid: String,
        currentPage: Int
    ): Result<Unit> = try {
        val historyRef = firestore.collection("histories").document()
        historyDto.historyId = historyRef.id
        historyRef.set(historyDto).await()

        val dateStr = historyDto.date?.toDate()?.let { ts ->
            val cal = Calendar.getInstance()
            cal.time = ts
            "%04d-%02d-%02d".format(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
        } ?: throw IllegalArgumentException("date is null")


        val querySnapshot = firestore.collection("daily_read_pages")
            .whereEqualTo("user_id", uid)
            .whereEqualTo("date", dateStr)
            .get()
            .await()

        if (querySnapshot.documents.isEmpty()) {
            val newDaily = DailyReadPageDto(
                userId = uid,
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

        val userBookSnapshot = firestore.collection("user_books")
            .whereEqualTo("user_id", uid)
            .whereEqualTo(FieldPath.documentId(), historyDto.userBookId)
            .get()
            .await()

        if (userBookSnapshot.documents.isNotEmpty()) {
            val userBookDoc = userBookSnapshot.documents.first()
            val totalReadTime = userBookDoc.getLong("total_read_time") ?: 0
            userBookDoc.reference.update(
                mapOf(
                    "current_page" to currentPage,
                    "total_read_time" to totalReadTime + historyDto.readTime
                )
            ).await()
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}