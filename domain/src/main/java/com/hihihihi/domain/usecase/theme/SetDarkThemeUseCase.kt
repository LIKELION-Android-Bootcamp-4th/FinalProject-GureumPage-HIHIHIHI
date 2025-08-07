package com.hihihihi.domain.usecase.theme

import com.hihihihi.domain.repository.ThemeRepository
import javax.inject.Inject

// 다크모드 상태 저장용 UserCase
class SetDarkThemeUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDarkTheme(enabled)
}