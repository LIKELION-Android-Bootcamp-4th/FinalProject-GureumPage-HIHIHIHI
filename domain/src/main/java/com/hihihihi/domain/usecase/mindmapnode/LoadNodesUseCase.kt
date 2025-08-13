package com.hihihihi.domain.usecase.mindmapnode

import com.hihihihi.domain.repository.MindmapNodeRepository
import javax.inject.Inject

class LoadNodesUseCase @Inject constructor(
    private val repository: MindmapNodeRepository
) {
    suspend operator fun invoke(mindmapId: String) = repository.loadNodes(mindmapId)
}