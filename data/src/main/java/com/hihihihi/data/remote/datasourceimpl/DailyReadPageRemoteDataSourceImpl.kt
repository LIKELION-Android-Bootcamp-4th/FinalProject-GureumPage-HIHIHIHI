package com.hihihihi.data.remote.datasourceimpl

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.remote.datasource.DailyReadPageRemoteDataSource
import com.hihihihi.data.remote.dto.DailyReadPageDto
import com.hihihihi.data.remote.dto.HistoryDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class DailyReadPageRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DailyReadPageRemoteDataSource {

    override fun getDailyReadPages(userId: String): Flow<List<DailyReadPageDto>> = callbackFlow {
        val listener = firestore.collection("daily_read_pages")
            .whereEqualTo("user_id", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val dailyReadPages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(DailyReadPageDto::class.java)?.apply { docId = doc.id }
                } ?: emptyList()

                trySend(dailyReadPages)
            }

        awaitClose { listener.remove() }
    }

    override fun getDailyReadPagesByUserIdAndDate(
        userId: String,
        dayOfStart: Date
    ): Flow<List<DailyReadPageDto>> {
        Log.d("Widget","HistoryHeatMapWorker : getDailyReadPagesByUserIdAndDate - userId:$userId , dayOfStart:${dayOfStart.toString()} ")

        val startOfDay = Timestamp(dayOfStart)
        return callbackFlow {
            val listenerRegistration = firestore.collection("daily_read_pages")
                .whereEqualTo("user_id", userId)
//                .whereGreaterThanOrEqualTo("date", startOfDay)
                .addSnapshotListener { snapshot, error ->
                    if (snapshot != null) {
                        snapshot.documents.forEach {
                            Log.e("Debug", "HistoryHeatMapWorker - date field: ${it.get("date")}")
                        }
                    }
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val dtos = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(DailyReadPageDto::class.java)
                    } ?: emptyList()

                    trySend(dtos)
                }

            awaitClose {
                listenerRegistration.remove()
            }
        }
    }
}