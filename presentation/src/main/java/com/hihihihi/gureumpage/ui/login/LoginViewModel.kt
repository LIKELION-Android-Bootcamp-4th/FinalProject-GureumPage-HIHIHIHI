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
    private suspend fun navigateAfterLogin(navController: NavHostController) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val isOnboardingComplete = getOnboardingCompleteUseCase(currentUser.uid).firstOrNull() ?: false

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
            signInWithGoogleUseCase(data)
            navigateAfterLogin(navController)
        }
    }

    fun kakaoLogin(
        navController: NavHostController
    ) {
        viewModelScope.launch {
            signInWithKakaoUseCase()
            navigateAfterLogin(navController)
        }

    }

    fun naverLogin(activity: Activity, navController: NavHostController) {
        viewModelScope.launch {
            signInWithNaverUseCase(activity)
            navigateAfterLogin(navController)
        }
    }
}
