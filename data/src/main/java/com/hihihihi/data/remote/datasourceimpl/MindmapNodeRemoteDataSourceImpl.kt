package com.hihihihi.data.remote.datasourceimpl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hihihihi.domain.operation.NodeEditOperation
import com.hihihihi.data.remote.datasource.MindmapNodeRemoteDataSource
import com.hihihihi.data.remote.dto.MindmapNodeDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MindmapNodeRemoteDataSourceImpl @Inject constructor(
    private val firebase: FirebaseFirestore
) : MindmapNodeRemoteDataSource {
    private fun nodesCollection() = firebase.collection("mindmap_nodes")

    override fun observe(mindmapId: String): Flow<List<MindmapNodeDto>> = callbackFlow {
        val collection = nodesCollection()
            .whereEqualTo("mindmapId", mindmapId)
            .whereEqualTo("deleted", false)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e("MindmapNodeRemoteDataSourceImpl", "OBS error", err)
                    close(err)
                    return@addSnapshotListener
                }
                val items = snap?.documents
                    ?.mapNotNull { it.toObject(MindmapNodeDto::class.java) }
                    ?: emptyList()

                Log.d("MindmapNodeRemoteDataSourceImpl", "OBS size=${items.size}")
                items.forEach { Log.d("MindmapNodeRemoteDataSourceImpl", "OBS id=${it.mindmapNodeId} parent=${it.parentNodeId} title=${it.nodeTitle} bookImage=${it.bookImage}") }
                trySend(items)
            }
        awaitClose { collection.remove() }
    }

    override suspend fun loadNodes(mindmapId: String): List<MindmapNodeDto> {
        val snap = nodesCollection()
            .whereEqualTo("mindmapId", mindmapId)
            .whereEqualTo("deleted", false)
            .get().await()
        Log.d("MindmapNodeRemoteDataSourceImpl", "LOAD size=${snap.documents.size}")
        snap.documents.forEach { Log.d("MindmapNodeRemoteDataSourceImpl", "LOAD id=${it.id} parent=${it.get("parent_node_id")} title=${it.get("node_title")}") }

        return snap.documents.mapNotNull { it.toObject(MindmapNodeDto::class.java) }
    }

    override suspend fun applyNodeOperation(
        mindmapId: String,
        operations: List<NodeEditOperation>
    ): Result<Unit> = runCatching {
        // 파이어스토어 배치가 500 쓰기 제한있음
        operations.chunked(450).forEach { chunk ->
            firebase.runBatch { batch ->
                chunk.forEach { operation ->
                    when (operation) {
                        is NodeEditOperation.Add -> {
                            val document = if (operation.node.mindmapNodeId.isBlank()) nodesCollection().document()
                            else nodesCollection().document(operation.node.mindmapNodeId)

                            val dto = operation.node.copy(
                                mindmapId = mindmapId,
                                mindmapNodeId = document.id,
                                deleted = false
                            )
                            Log.d("MindmapNodeRemoteDataSourceImpl", "ADD[$chunk] id=${dto.mindmapNodeId} parent=${dto.parentNodeId} title=${dto.nodeTitle}")

                            batch.set(document, dto)
                        }

                        is NodeEditOperation.Update -> {
                            requireNotNull(operation.node.mindmapNodeId.takeIf { it.isNotBlank() }) { "노드 아이디가 필요합니다" }
                            batch.set(
                                nodesCollection().document(operation.node.mindmapNodeId),
                                operation.node.copy(mindmapId = mindmapId),
                                SetOptions.merge()
                            )
                        }

                        is NodeEditOperation.Delete -> {
                            batch.update(
                                nodesCollection().document(operation.nodeId),
                                mapOf("deleted" to true)
                            )
                        }
                    }
                }
            }.await()
            Log.d("MindmapNodeRemoteDataSourceImpl", "chunk[$chunk] committed")
        }
    }
}
