package com.hihihihi.domain.usecase.theme

import com.hihihihi.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//다크모드 상태 조회용 UserCase
class GetDarkThemeUserCase @Inject constructor(
    private val repository: ThemeRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isDarkTheme()
}