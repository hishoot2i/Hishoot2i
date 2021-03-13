#
-keepattributes SourceFile,LineNumberTable
#
-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
-dontwarn org.conscrypt.ConscryptHostnameVerifier
# Android API 30; atm. target is Android 29
-dontwarn android.view.WindowInsetsAnimationControlListener
-dontwarn android.view.WindowInsetsController$OnControllableInsetsChangedListener
-dontwarn android.view.WindowInsetsAnimation$Callback
