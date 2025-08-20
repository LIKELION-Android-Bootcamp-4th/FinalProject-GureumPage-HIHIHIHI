package com.hihihihi.data.repotisoryimpl

import com.hihihihi.data.mapper.toDomain
import com.hihihihi.data.remote.datasource.MindmapNodeRemoteDataSource
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.operation.NodeEditOperation
import com.hihihihi.domain.repository.MindmapNodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MindmapNodeRepositoryImpl @Inject constructor(
    private val remoteDataSource: MindmapNodeRemoteDataSource,
) : MindmapNodeRepository {
    override fun observe(mindmapId: String): Flow<List<MindmapNode>> =
        remoteDataSource.observe(mindmapId).map { dtoList -> dtoList.map { it.toDomain() } }

    override suspend fun loadNodes(mindmapId: String): List<MindmapNode> =
        remoteDataSource.loadNodes(mindmapId).map { it.toDomain() }

    override suspend fun applyNodeOperation(
        mindmapId: String,
        operations: List<NodeEditOperation>
    ): Result<Unit> = remoteDataSource.applyNodeOperation(
        mindmapId,
        operations.map {
            when (it) {
                is NodeEditOperation.Add -> NodeEditOperation.Add(it.node)
                is NodeEditOperation.Update -> NodeEditOperation.Update(it.node)
                is NodeEditOperation.Delete -> NodeEditOperation.Delete(it.nodeId)
            }
        }
    )
}