import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.oss.licenses)
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { load(it) }
    }
}

fun getLocalProperty(key: String): String {
    return localProperties[key]?.toString() ?: ""
}

android {
    namespace = "com.hihihihi.gureumpage"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hihihihi.gureumpage"
        minSdk = 26
        targetSdk = 35
        versionCode = 10
        versionName = "1.1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val naverId = getLocalProperty("NAVER_CLIENT_ID")
        val naverSecret = getLocalProperty("NAVER_CLIENT_SECRET")
        val kakaoKey = getLocalProperty("KAKAO_NATIVE_APP_KEY")

        buildConfigField("String", "NAVER_CLIENT_ID", "\"$naverId\"")
        buildConfigField("String", "NAVER_CLIENT_SECRET", "\"$naverSecret\"")
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
        buildConfigField("String", "VERSION_NAME", "\"$versionName\"")

        manifestPlaceholders += mapOf(
            "NAVER_CLIENT_ID" to naverId,
            "NAVER_CLIENT_SECRET" to naverSecret,
            "KAKAO_NATIVE_APP_KEY" to kakaoKey
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true // 릴리즈 모드 난독화 활성화가 권장 사항
            isShrinkResources = true // 미사용 리소스 제거
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation(project(":presentation"))
    implementation(project(":data"))
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.play.services.oss.licenses)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.kakao.user)
    implementation(libs.naver.oauth)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.hilt)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
