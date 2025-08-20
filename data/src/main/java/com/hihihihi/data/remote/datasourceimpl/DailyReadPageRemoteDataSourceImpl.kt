package com.hihihihi.data.remote.datasourceimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.data.remote.datasource.DailyReadPageRemoteDataSource
import com.hihihihi.data.remote.dto.DailyReadPageDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DailyReadPageRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DailyReadPageRemoteDataSource {

    override suspend fun getDailyReadPages(userId: String): List<DailyReadPageDto> {
        val snapshot = firestore.collection("daily_read_pages")
            .whereEqualTo("user_id", userId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(DailyReadPageDto::class.java)?.apply { docId = doc.id }
        }
    }
}