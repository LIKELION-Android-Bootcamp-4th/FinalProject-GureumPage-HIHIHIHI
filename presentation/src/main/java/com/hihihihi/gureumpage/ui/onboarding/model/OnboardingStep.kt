package com.hihihihi.gureumpage.ui.onboarding.model

sealed class OnboardingStep {
    object Welcome : OnboardingStep()
    object Nickname : OnboardingStep()
    object Purpose : OnboardingStep()
    object Feature : OnboardingStep()
    object Theme : OnboardingStep()
    object Finish : OnboardingStep()
}