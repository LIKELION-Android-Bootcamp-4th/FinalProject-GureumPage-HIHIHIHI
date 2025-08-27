package com.hihihihi.gureumpage.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.hihihihi.domain.usecase.auth.SignInWithGoogleUseCase
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hihihihi.domain.usecase.auth.SignInWithKakaoUseCase
import com.hihihihi.domain.usecase.auth.SignInWithNaverUseCase
import com.hihihihi.domain.usecase.user.GetLastProviderUseCase
import com.hihihihi.domain.usecase.user.GetOnboardingCompleteUseCase
import com.hihihihi.domain.usecase.user.GetUserUseCase
import com.hihihihi.domain.usecase.user.SetOnboardingCompleteUseCase
import com.hihihihi.domain.usecase.user.SetLastProviderUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithKakaoUseCase: SignInWithKakaoUseCase,
    private val signInWithNaverUseCase: SignInWithNaverUseCase,
    private val getOnboardingCompleteUseCase: GetOnboardingCompleteUseCase,
    private val setOnboardingCompleteUseCase: SetOnboardingCompleteUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val setLastProviderUseCase: SetLastProviderUseCase,
    private val getLastProviderUseCase: GetLastProviderUseCase,
) : ViewModel() {
    private val TAG = "AuthViewModel"

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                lastProvider = getLastProvider(),
                errorMessage = null
            )
        }
    }

    suspend fun getLastProvider(): String{
        return getLastProviderUseCase().first()
    }

    private suspend fun navigateAfterLogin(navController: NavHostController) {
        setLoading(true, "사용자 정보를 설정하는 중...")

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            setError("로그인 정보를 찾을 수 없습니다")
            return
        }

        try {
            waitForUserDocumentCreation(currentUser.uid)
        } catch (e: Exception) {
            setError("사용자 정보 설정에 실패했습니다. 다시 시도해주세요.")
            return
        }

        setLoading(true, "사용자 정보를 확인하는 중...")

        val profile = getUserUseCase(currentUser.uid)
        val hasNickname = !profile?.nickname.isNullOrBlank()
        if (hasNickname) setOnboardingCompleteUseCase(currentUser.uid, true)

        val isOnboardingComplete = getOnboardingCompleteUseCase(currentUser.uid).firstOrNull() ?: false

        val destination = if (isOnboardingComplete && hasNickname) {
            NavigationRoute.Home.route
        } else {
            NavigationRoute.OnBoarding.route
        }

        setLoading(true, "사용자 정보를 확인하는 중...")
        navController.navigate(destination) {
            popUpTo(NavigationRoute.Login.route) { inclusive = true }
        }
    }

    private suspend fun waitForUserDocumentCreation(uid: String, maxRetries: Int = 10) {
        val firestore = FirebaseFirestore.getInstance()
        var retryCount = 0

        while (retryCount < maxRetries) {
            try {
                val document = firestore.collection("users").document(uid)
                    .get()
                    .await()

                if (document.exists()) {
                    return
                }

                delay(1000)
                retryCount++

            } catch (e: Exception) {
                if (retryCount == maxRetries - 1) {
                    throw e
                }
                delay(1000)
                retryCount++
            }
        }

        throw Exception("사용자 문서 생성 시간이 초과되었습니다")
    }

    private fun setLoading(isLoading: Boolean, message: String = "") {
        _uiState.value = _uiState.value.copy(
            isLoading = isLoading,
            loadingMessage = message,
            errorMessage = null
        )
    }

    private fun setError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun googleLogin(
        context: Context,
        launcher: ActivityResultLauncher<Intent>
    ) {
        setLoading(true, "구글 로그인 중...")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // strings.xml에 있어야 함
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(context, gso)
        launcher.launch(client.signInIntent)
    }

    fun handleGoogleSignInResult(
        data: Intent,
        navController: NavHostController
    ) {
        viewModelScope.launch {
            try {
                setLoading(true, "구글 로그인 처리 중...")
                signInWithGoogleUseCase(data)
                setLastProviderUseCase("google")
                navigateAfterLogin(navController)
            } catch (e: Exception) {
                setError("구글 로그인에 실패했습니다. 다시 시도해주세요.")
            }
        }
    }

    fun kakaoLogin(
        navController: NavHostController
    ) {
        viewModelScope.launch {
            try {
                setLoading(true, "카카오 로그인 중...")
                signInWithKakaoUseCase()
                setLastProviderUseCase("kakao")
                navigateAfterLogin(navController)
            } catch (e: Exception) {
                setError("카카오 로그인에 실패했습니다. 다시 시도해주세요.")
            }
        }

    }

    fun naverLogin(activity: Activity, navController: NavHostController) {
        viewModelScope.launch {
            try {
                setLoading(true, "네이버 로그인 중...")
                signInWithNaverUseCase(activity)
                setLastProviderUseCase("naver")
                navigateAfterLogin(navController)
            } catch (e: Exception) {
                setError("네이버 로그인에 실패했습니다. 다시 시도해주세요.")
            }
        }
    }
}
