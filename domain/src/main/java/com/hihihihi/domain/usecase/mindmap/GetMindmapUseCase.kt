package com.hihihihi.domain.usecase.mindmap

import com.hihihihi.domain.repository.MindmapRepository
import javax.inject.Inject

class GetMindmapUseCase @Inject constructor(
    private val repository: MindmapRepository
) {
    suspend operator fun invoke(mindmapId: String) = repository.getMindmap(mindmapId)
}