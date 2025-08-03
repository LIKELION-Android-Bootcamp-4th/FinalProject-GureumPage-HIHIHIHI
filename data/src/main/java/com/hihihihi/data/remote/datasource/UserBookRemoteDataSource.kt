package com.hihihihi.data.remote.datasource

import com.hihihihi.data.remote.dto.UserBookDto
import kotlinx.coroutines.flow.Flow

// Firestore 등 원격 데이터 소스에 접근하기 위한 인터페이스 정의
interface UserBookRemoteDataSource {
    // 특정 사용자(userId)의 책 목록을 비동기 스트림(Flow) 형태로 반환
    fun getUserBooks(userId: String): Flow<List<UserBookDto>>
}
