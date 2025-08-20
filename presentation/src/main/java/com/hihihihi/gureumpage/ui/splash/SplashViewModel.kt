package com.hihihihi.gureumpage.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hihihihi.domain.usecase.user.GetOnboardingCompleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getOnboardingCompleteUseCase: GetOnboardingCompleteUseCase
) : ViewModel() {

    sealed interface NavTarget {
        data object Loading : NavTarget
        data object Login : NavTarget
        data object Onboarding : NavTarget
        data object Home : NavTarget
    }

    private val _nav = MutableStateFlow<NavTarget>(NavTarget.Loading)
    val nav: StateFlow<NavTarget> = _nav

    private val auth = FirebaseAuth.getInstance()

    init {
        auth.addAuthStateListener { fb ->
            viewModelScope.launch {
                delay(3000)
                val user = fb.currentUser
                if (user == null) {
                    _nav.value = NavTarget.Login
                } else {
                    viewModelScope.launch {
                        val done = getOnboardingCompleteUseCase(user.uid).firstOrNull() ?: false
                        _nav.value = if (done) NavTarget.Home else NavTarget.Onboarding
                    }
                }
            }
        }
    }
}