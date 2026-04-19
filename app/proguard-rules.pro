# Kakao SDK
-keep class com.kakao.sdk.**.model.* { <fields>; }

# OkHttp (https://github.com/square/okhttp/pull/6792)
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

# Retrofit2 (R8 full mode)
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Gson — @SerializedName 필드 obfuscation 방지
-keepattributes Signature
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# kotlinx-serialization (generic)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# kotlinx-serialization (com.hihihihi 패키지)
-keep,includedescriptorclasses class com.hihihihi.**$$serializer { *; }
-keepclassmembers class com.hihihihi.** {
    *** Companion;
}
-keepclasseswithmembers class com.hihihihi.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Firebase Firestore — @PropertyName getter/setter 보존
-keepclassmembers class com.hihihihi.data.remote.dto.** {
    <init>();
    *** get*();
    void set*(***);
}

# Naver OAuth
-dontwarn com.navercorp.nid.**

# 스택 트레이스 줄 번호 보존
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
