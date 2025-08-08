package com.hihihihi.gureumpage.ui.onboarding.model

sealed class OnboardingStep {
    object Welcome : OnboardingStep()
    object Nickname : OnboardingStep()
    object Purpose : OnboardingStep() // TODO 좀 더 생각 필요
    object Feature : OnboardingStep()
    object Theme : OnboardingStep()
    object Finish : OnboardingStep()
}