package com.hihihihi.domain.usecase.mindmapnode

import com.hihihihi.domain.operation.NodeEditOperation
import com.hihihihi.domain.repository.MindmapNodeRepository
import javax.inject.Inject

class ApplyNodeOperation @Inject constructor(
    private val repository: MindmapNodeRepository
) {
    suspend operator fun invoke(mindmapId: String, operations: List<NodeEditOperation>) =
        repository.applyNodeOperation(mindmapId, operations)
}