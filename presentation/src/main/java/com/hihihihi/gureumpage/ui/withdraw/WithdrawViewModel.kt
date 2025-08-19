package com.hihihihi.gureumpage.ui.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.functions
import com.hihihihi.domain.usecase.user.ClearUserDataUseCase
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val clearUserDataUseCase: ClearUserDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WithdrawUiState())
    val uiState: StateFlow<WithdrawUiState> = _uiState.asStateFlow()

    //탈퇴 이벤트
    private val _withdrawEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val withdrawEvent: SharedFlow<Unit> = _withdrawEvent.asSharedFlow()

    fun withdrawUser() = viewModelScope.launch {
        _uiState.update { it.copy(loading = true, error = null) }

        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                _uiState.update { it.copy(loading = false, error = "로그인된 사용자가 없습니다") }
                return@launch
            }

            val providerId = getProviderFromFirestore(currentUser.uid)

            // 1. 먼저 소셜 플랫폼에서 연결 해제
            when (providerId) {
                "kakao" -> {
                    unlinkKakaoAccount()
                }

                "naver" -> {
                    unlinkNaverAccount()
                }

                "google.com" -> {}

                else -> {}
            }

            // 2. Firebase Functions 호출 (Firestore + Auth 삭제)
            val functions = Firebase.functions
            val deleteAccount = functions.getHttpsCallable("deleteUserAccount")
            val result = deleteAccount.call().await()

            clearUserDataUseCase.clearAll()

            // 3. 상태 초기화 및 로그아웃 이벤트 발생
            _uiState.value = WithdrawUiState(loading = false)
            _withdrawEvent.tryEmit(Unit)
        } catch (e: Exception) {

            val errorMessage = when {
                e.message?.contains("unauthenticated") == true -> "인증이 필요합니다"
                e.message?.contains("not-found") == true -> "사용자를 찾을 수 없습니다"
                e.message?.contains("permission-denied") == true -> "권한이 없습니다"
                else -> "탈퇴 처리 중 오류가 발생했습니다: ${e.message}"
            }

            _uiState.update { it.copy(loading = false, error = errorMessage) }
        }
    }

    // Firestore에서 provider 가져오는 함수
    private suspend fun getProviderFromFirestore(uid: String): String? {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            val userDoc = firestore.collection("users").document(uid).get().await()
            val provider = userDoc.getString("provider")
            provider
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun unlinkKakaoAccount() {
        return suspendCoroutine { continuation ->
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else {
                    continuation.resume(Unit)
                }
            }
        }
    }

    private suspend fun unlinkNaverAccount() {
        return suspendCoroutine { continuation ->
            NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
                override fun onSuccess() {
                    continuation.resume(Unit)
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    continuation.resumeWithException(Exception(message))
                }

                override fun onError(errorCode: Int, message: String) {
                    continuation.resumeWithException(Exception(message))
                }
            })
        }
    }
}