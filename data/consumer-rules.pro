# Gson @SerializedName 필드 보존
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Firebase Firestore @PropertyName getter/setter 보존
-keepclassmembers class com.hihihihi.data.remote.dto.** {
    <init>();
    *** get*();
    void set*(***);
}
