import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) load(localFile.inputStream())
}

android {
    namespace = "com.hihihihi.presentation"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val googleWebClientId = localProperties["GOOGLE_WEB_CLIENT_ID"]?.toString().orEmpty()
        if (googleWebClientId.isBlank()) {
            logger.warn("GOOGLE_WEB_CLIENT_ID가 비어 있습니다. local.properties를 확인해주세요.")
        }
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
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
    // CA
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.play.services.oss.licenses)

    // MindMap
    implementation(libs.gyso.treeview)
    implementation(libs.androidx.dynamicanimation)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.coroutines)
    
    implementation(libs.bundles.hilt)
    // hiltViewModel의 신 패키지 제공 아티팩트 (androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    // Navigation 기반 ViewModel 스코프 사용 시 필요
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.bundles.glance)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.social.auth)
    implementation(libs.bundles.compose.extra)
    implementation(libs.kotlinx.serialization.json)

    // Google Identity (googleid) SDK가 gson을 transitive 의존성으로 요구함
    // kotlinx.serialization과 중복 사용 안 함 — 신규 JSON 코드는 kotlinx.serialization 사용할 것
    implementation(libs.gson)

    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
