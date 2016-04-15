## Dart v1
-dontwarn com.f2prateek.dart.internal.**
-keep class **$$ExtraInjector {*;}
-keepclasseswithmembernames class *{    @com.f2prateek.dart.* <fields>;}

#for dart 2.0 only
-keep class **Henson { *; }
-keep class **$$IntentBuilder { *; }


#if you use it
#see Parceler's github page
#for specific proguard instructions