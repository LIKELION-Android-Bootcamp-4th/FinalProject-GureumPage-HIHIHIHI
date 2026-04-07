import java.util.Properties

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        load(localFile.inputStream())
    }
}

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.hihihihi.presentation"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
}

dependencies {
    // CA
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)

    // Retrofit + Gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coroutines + Flow
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Hilt (의존성 주입)
    implementation(libs.hilt.android)
    implementation("androidx.hilt:hilt-work:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    ksp(libs.hilt.android.compiler)
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // MindMap
    implementation(libs.gyso.treeview)
    implementation(libs.androidx.dynamicanimation)
    implementation(libs.androidx.ui.viewbinding)

    // Coil
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")


    implementation(libs.androidx.datastore.preferences)

    // UI 관련 라이브러리
    implementation(libs.calendar.compose)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.airbnb.android:lottie-compose:5.2.0")
    implementation("com.github.a914-gowtham:compose-ratingbar:1.3.12")
    implementation("androidx.compose.material:material-icons-extended")

    implementation(libs.google.play.review)
    implementation(libs.google.play.review.ktx)
    implementation(libs.play.services.oss.licenses)

    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)

    implementation(libs.androidx.work.runtime.ktx)
//    implementation("androidx.work:work-runtime-ktx:2.9.0")

    implementation("androidx.core:core-splashscreen:1.0.1")

    // 소셜 로그인 Auth SDK
    implementation("com.google.android.gms:play-services-auth:21.4.0")
    implementation("com.kakao.sdk:v2-user:2.20.3")
    implementation("com.navercorp.nid:oauth:5.9.0")
}
