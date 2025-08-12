package com.hihihihi.data.remote.datasourceimpl

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
            .whereEqualTo("mindmap_id", mindmapId)
            .whereEqualTo("deleted", false)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
                val items = snap?.documents
                    ?.mapNotNull { it.toObject(MindmapNodeDto::class.java) }
                    ?: emptyList()
                trySend(items)
            }
        awaitClose { collection.remove() }
    }

    override suspend fun loadNodes(mindmapId: String): List<MindmapNodeDto> {
        val snap = nodesCollection()
            .whereEqualTo("mindmap_id", mindmapId)
            .whereEqualTo("deleted", false)
            .get().await()

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
        }
    }
}
