# Keep Room database components
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.internal.ReactorRoomDatabase

# Keep Kotlin Serialization classes
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <init>(...);
}
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Compose nodes and animation properties if needed
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
