# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /files/default-proguard-rules.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Gson
-keep class com.google.gson.** { *; }

# API Models (Keep names for JSON serialization)
-keep class ug.global.temp.network.** { *; }

# Room
-keep class androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.ColumnInfo <fields>;
    @androidx.room.Embedded <fields>;
    @androidx.room.Relation <fields>;
    @androidx.room.ForeignKey <fields>;
    @androidx.room.Ignore <fields>;
    @androidx.room.PrimaryKey <fields>;
    @androidx.room.Transaction <methods>;
}

# Hilt / Dagger
-keep class com.google.dagger.hilt.** { *; }
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.annotation.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.CoroutineExceptionHandler {
    <init>(...);
}

# Coil
-keep class coil.** { *; }