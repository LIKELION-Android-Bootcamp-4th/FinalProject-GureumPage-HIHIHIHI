package com.hihihihi.domain.usecase.mindmap

import com.hihihihi.domain.model.Mindmap
import com.hihihihi.domain.repository.MindmapRepository
import javax.inject.Inject

class CreateMindmapUseCase @Inject constructor(
    private val repository: MindmapRepository
) {
    suspend operator fun invoke(mindmap: Mindmap) = repository.createMindmap(mindmap)
}