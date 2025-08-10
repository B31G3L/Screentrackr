# Screentrackr ProGuard Rules
# Erweiterte Konfiguration für aufgeräumte App-Version

# ========== GENERAL ANDROID ==========
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# ========== KEEP TRACKING VALUES ==========
# TrackingValues und Enums müssen erhalten bleiben (Serializable)
-keep class com.beigel.screenTracker.TrackingValues { *; }
-keep class com.beigel.screenTracker.TrackingValues$** { *; }

# Enum-Klassen komplett erhalten
-keepclassmembers enum com.beigel.screenTracker.TrackingValues$* {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ========== KEEP APP CONSTANTS ==========
# AppConstants komplett erhalten (werden zur Laufzeit referenziert)
-keep class com.beigel.screenTracker.AppConstants { *; }
-keep class com.beigel.screenTracker.AppConstants$** { *; }

# ========== UTILITIES CLASS ==========
# Utilities-Methoden erhalten (werden reflektiv aufgerufen)
-keep class com.beigel.screenTracker.Utilities {
    public static <methods>;
}

# ========== ACTIVITIES ==========
# Activities und ihre wichtigen Methoden
-keep public class * extends android.app.Activity
-keep public class * extends androidx.appcompat.app.AppCompatActivity

# MainActivity spezifische Keeps
-keep class com.beigel.screenTracker.MainActivity {
    public <methods>;
    private void startTracking();
}

# Trackingscreen spezifische Keeps
-keep class com.beigel.screenTracker.Trackingscreen {
    public <methods>;
}

# ========== SETTINGS MANAGER ==========
# SettingsManager Interface und Implementation
-keep class com.beigel.screenTracker.SettingsManager { *; }
-keep interface com.beigel.screenTracker.SettingsManager$SettingsListener { *; }

# ========== VIEW BINDING ==========
# View Binding Klassen erhalten
-keep class * implements androidx.viewbinding.ViewBinding {
    public static <methods>;
    public <methods>;
}

# ========== COLOR PICKER ==========
# ColorPickerView Library
-keep class com.skydoves.colorpickerview.** { *; }
-dontwarn com.skydoves.colorpickerview.**

# ========== ANDROID COMPONENTS ==========
# Standard Android Components
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# ========== SERIALIZABLE ==========
# Serializable Support
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ========== ANNOTATIONS ==========
# Keep Annotations
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes AnnotationDefault
-keepattributes SourceFile,LineNumberTable
-keep class androidx.annotation.** { *; }

# ========== LOGGING ==========
# Remove Log calls in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Keep System.out.println for fallback logging
-keep class java.lang.System {
    public static java.io.PrintStream out;
}

# ========== WEBVIEW (falls verwendet) ==========
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# ========== REFLECTION ==========
# Wenn Reflection verwendet wird, diese Patterns anpassen
-keepclassmembers class * {
    @androidx.annotation.Keep <methods>;
    @androidx.annotation.Keep <fields>;
    @androidx.annotation.Keep <init>(...);
}

# ========== APP-SPECIFIC ==========
# Marker-Creation Methods (falls dynamisch aufgerufen)
-keepclassmembers class com.beigel.screenTracker.** {
    *Marker*(...);
    *Preview*(...);
    *Color*(...);
}

# Settings-Related Methods
-keepclassmembers class com.beigel.screenTracker.** {
    *Settings*(...);
    *Preference*(...);
}

# ========== DEBUGGING ==========
# Uncomment für Release-Debugging
# -printmapping mapping.txt
# -printseeds seeds.txt
# -printusage usage.txt