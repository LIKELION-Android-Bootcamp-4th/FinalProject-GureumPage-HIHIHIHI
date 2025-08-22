import java.util.Properties

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        load(localFile.inputStream())
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.oss.licenses)
}

android {
    namespace = "com.hihihihi.gureumpage"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hihihihi.gureumpage"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField ("String", "KAKAO_NATIVE_APP_KEY", "\"${localProperties["KAKAO_NATIVE_APP_KEY"] ?: ""}\"")
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = localProperties["KAKAO_NATIVE_APP_KEY"] ?: ""
        buildConfigField ("String", "NAVER_CLIENT_ID", "\"${localProperties["NAVER_CLIENT_ID"] ?: ""}\"")
        manifestPlaceholders["NAVER_CLIENT_ID"] = localProperties["NAVER_CLIENT_ID"] ?: ""
        buildConfigField ("String", "NAVER_CLIENT_SECRET", "\"${localProperties["NAVER_CLIENT_SECRET"] ?: ""}\"")
        manifestPlaceholders["NAVER_CLIENT_SECRET"] = localProperties["NAVER_CLIENT_SECRET"] ?: ""
        buildConfigField ("String", "VERSION_NAME", "\"${versionName}\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            signingConfig = signingConfigs.getByName("debug")
//        }
//        debug {
//            applicationIdSuffix = ".debug"
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
    implementation(project(":data"))
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.functions)
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
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    implementation(libs.jetbrains.kotlinx.coroutines.android)

    // Hilt (의존성 주입)
    implementation(libs.hilt.android.v2562)
    ksp(libs.hilt.android.compiler)

    // MindMap
    implementation(libs.gyso.treeview)
    implementation(libs.androidx.dynamicanimation)
    implementation(libs.androidx.ui.viewbinding)

    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.kizitonwose.calendar:compose:2.8.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("com.airbnb.android:lottie-compose:5.2.0")

    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging")

    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.android.gms:play-services-auth:21.4.0")

    // kakao auth
    implementation("com.kakao.sdk:v2-user:2.20.3")

    // naver auth
    implementation("com.navercorp.nid:oauth:5.9.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.calendar.compose)

    implementation(libs.google.play.review)
    implementation(libs.google.play.review.ktx)

    implementation(libs.play.services.oss.licenses)

    implementation ("com.github.a914-gowtham:compose-ratingbar:1.3.12")

    implementation ("androidx.core:core-splashscreen:1.0.1")

}