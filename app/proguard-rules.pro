# ButterKnife 7
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Crashlytics 1.+
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**
-keep public class * extends java.lang.Exception
-dontwarn com.crashlytics.**
-printmapping mapping.txt

## GSON 2.2.4 specific rules ##
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# For using GSON @Expose annotation
-keepattributes *Annotation*
-keepattributes EnclosingMethod
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

#Dart v1
-dontwarn com.f2prateek.dart.internal.**
-keep class **$$ExtraInjector {*;}
-keepclasseswithmembernames class *{
    @com.f2prateek.dart.* <fields>;
}
-keepnames class * { @com.f2prateek.dart.InjectView *;}
# LeakCanary
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }
#Support Design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

#Support AppCompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

#Support CardView
# http://stackoverflow.com/q/29679177/
-keep class android.support.v7.widget.RoundRectDrawable { *; }

# Dagger
-dontwarn dagger.internal.codegen.**
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}
-keep class dagger.* { *; }
-keep class javax.inject.* { *; }
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection
	
#??
-keepattributes  Signature,SourceFile,LineNumberTable,InnerClasses
-keep public class net.grandcentrix.tray.** {*;}
-keep public class org.illegaller.ratabb.hishoot2i.model.tray.** {*;}
-keep public class org.illegaller.ratabb.hishoot2i.di.Modules.** { *;}
-keepclassmembers public class org.illegaller.ratabb.hishoot2i.di.Modules.** { *;}
-keep public class org.illegaller.ratabb.hishoot2i.HishootApplication.** {*;}
-keep class io.fabric.sdk.** {*;}
-keep class android.support.v4.widget.** { *;}
-keep class android.support.v7.widget.** { *;}
-keep class android.support.design.widget.** { *;}



-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*
#-dontwarn

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep fragments

-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

# Serializables

-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Native Methods

-keepclasseswithmembernames class * {
    native <methods>;
}

# Android Support Library

-keep class android.** {*;}

# Button methods

-keepclassmembers class * {

public void *ButtonClicked(android.view.View);

}

# Remove Logging
-assumenosideeffects class android.util.Log {
    public static *** e(...);
    public static *** w(...);
    public static *** wtf(...);
    public static *** d(...);
    public static *** v(...);
}