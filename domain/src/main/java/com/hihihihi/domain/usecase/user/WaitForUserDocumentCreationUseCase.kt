package com.hihihihi.domain.usecase.user

import com.hihihihi.domain.repository.UserRepository
import com.hihihihi.domain.util.runSuspendCatching
import kotlinx.coroutines.delay
import javax.inject.Inject

class WaitForUserDocumentCreationUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String, maxRetries: Int = 10): Result<Unit> = runSuspendCatching {
        var retryCount = 0
        while (retryCount < maxRetries) {
            val user = userRepository.getUser(uid)
            if (user != null) {
                return@runSuspendCatching
            }
            retryCount++
            delay(1000)
        }
        throw Exception("사용자 문서 생성 대기 시간 초과")
    }
}
