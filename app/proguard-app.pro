# [Proguard:6.0]
# We can debug the ProGuard configuration by instrumenting the code and
# checking the log for feedback. Disable the option again for actual releases!
#-addconfigurationdebugging
# Specifically target Android.
-android

# Keep SourceFile names & Line Numbers for stack traces. (Note: If we are really security concious,
# we should remove this line.
-keepattributes SourceFile,LineNumberTable


# Dagger2
-dontwarn com.google.errorprone.annotations.*

# BottomNavigationView disable shifting.
-keepclassmembers class android.support.design.internal.BottomNavigationMenuView {
    private boolean mShiftingMode;
}
-keepclassmembers class android.support.design.internal.BottomNavigationItemView {
    private int mDefaultMargin;
}

# Template Manager searching
-keep class android.support.v7.widget.SearchView { *; }