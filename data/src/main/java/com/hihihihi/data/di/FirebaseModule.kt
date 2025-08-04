package com.hihihihi.data.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // 앱 전체 라이프사이클 동안 싱글톤으로 제공
object FirebaseModule {

    // FirebaseFirestore 인스턴스 의존성 주입 제공
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        // FirebaseFirestore 싱글톤 인스턴스 반환
        return FirebaseFirestore.getInstance()
    }

}