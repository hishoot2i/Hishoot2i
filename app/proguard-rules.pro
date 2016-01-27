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
## Dart v1
-dontwarn com.f2prateek.dart.internal.**
-keep class **$$ExtraInjector {*;}
-keepclasseswithmembernames class *{
    @com.f2prateek.dart.* <fields>;
}
-keepnames class * { @com.f2prateek.dart.InjectView *;}

## Dagger
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

## Otto
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

## Support Widget
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v4.widget.** { *; }

####
#-keepattributes  Signature,SourceFile,LineNumberTable,InnerClasses
-keep class org.illegaller.ratabb.hishoot2i.model.template.ModelHtz {*;}
-keep class org.illegaller.ratabb.hishoot2i.model.template.ModelV2 {*;}