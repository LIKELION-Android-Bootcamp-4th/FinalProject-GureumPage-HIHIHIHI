package com.hihihihi.gureumpage.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.hihihihi.domain.usecase.auth.SignInWithKakaoUseCase
import com.hihihihi.domain.usecase.auth.SignInWithNaverUseCase
import com.hihihihi.domain.usecase.user.GetOnboardingCompleteUseCase
import kotlinx.coroutines.flow.firstOrNull

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithKakaoUseCase: SignInWithKakaoUseCase,
    private val signInWithNaverUseCase: SignInWithNaverUseCase,
    private val getOnboardingCompleteUseCase: GetOnboardingCompleteUseCase
) : ViewModel() {
    private val TAG = "AuthViewModel"

    private suspend fun navigateAfterLogin(navController: NavHostController) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        Log.d(TAG, "유저 정보: ${currentUser.uid}")

        val isOnboardingComplete = getOnboardingCompleteUseCase(currentUser.uid).firstOrNull() ?: false
        Log.d(TAG, "온보딩 완료 상태: $isOnboardingComplete")

        val destination = if (isOnboardingComplete) {
            NavigationRoute.Home.route
        } else {
            NavigationRoute.OnBoarding.route
        }

        navController.navigate(destination) {
            popUpTo(NavigationRoute.Login.route) { inclusive = true }
        }
    }

    fun googleLogin(
        context: Context,
        launcher: ActivityResultLauncher<Intent>
    ) {
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
                signInWithGoogleUseCase(data)
                Log.d(TAG, "구글 로그인 성공")
                navigateAfterLogin(navController)
            } catch (e: Exception) {
                Log.e(TAG, "구글 로그인 실패", e)
            }
        }
    }

    fun kakaoLogin(
        navController: NavHostController
    ) {
        viewModelScope.launch {
            try {
                signInWithKakaoUseCase()
                Log.d(TAG, "카카오 로그인 성공")
                navigateAfterLogin(navController)
            } catch (e: Exception) {
                Log.d(TAG, "카카오 로그인 성공")
            }
        }

    }

    fun naverLogin(activity: Activity, navController: NavHostController) {
        viewModelScope.launch {
            try {
                signInWithNaverUseCase(activity)
                Log.d(TAG, "네이버 로그인 성공")
                navigateAfterLogin(navController)
            } catch (e: Exception) {
                Log.e(TAG, "네이버 로그인 실패", e)
            }
        }
    }
}
