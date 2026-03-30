package com.hihihihi.domain.usecase.mindmapnode

import com.hihihihi.domain.repository.MindmapNodeRepository
import javax.inject.Inject

class ObserveMindmapNodeUseCase @Inject constructor(
    private val repository: MindmapNodeRepository
) {
    operator fun invoke(mindmapId: String) = repository.observe(mindmapId)
}