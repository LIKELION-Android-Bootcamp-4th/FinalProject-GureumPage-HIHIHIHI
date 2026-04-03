package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.model.GureumThemeType
import com.hihihihi.domain.repository.UserPreferencesRepository
import com.hihihihi.domain.util.runSuspendCatching
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(theme: GureumThemeType): Result<Unit> = runSuspendCatching {
        repository.setTheme(theme)
    }
}
