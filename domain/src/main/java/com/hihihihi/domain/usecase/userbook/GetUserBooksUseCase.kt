package com.hihihihi.domain.usecase.userbook

import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.repository.UserBookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// 사용자 책 목록을 가져오는 UseCase
class GetUserBooksUseCase @Inject constructor(
    private val repository: UserBookRepository // UserBook 데이터에 접근하는 Repository 주입
) {
    // invoke 연산자 함수로 UseCase를 함수처럼 호출 가능
    operator fun invoke(userId: String): Flow<List<UserBook>> {
        // Repository에서 Flow 형태로 사용자 책 목록을 리턴받음
        return repository.getUserBooks(userId)
    }
}