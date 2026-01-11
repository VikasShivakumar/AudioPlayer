# Keep Media3 classes
-keep class androidx.media3.** { *; }
-keepclassmembers class androidx.media3.** { *; }

# Keep Room database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep serialization classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
