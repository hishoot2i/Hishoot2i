
#
-dontobfuscate
-keepattributes SourceFile, LineNumberTable

-allowaccessmodification
#noinspection ShrinkerUnresolvedReference
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
#noinspection ShrinkerUnresolvedReference
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
#noinspection ShrinkerUnresolvedReference
-keep class kotlin.reflect.jvm.internal.ReflectionFactoryImpl

#-dontwarn nl.adaptivity.xmlutil.*
#-dontnote nl.adaptivity.xmlutil.*

##
-keep class MainCli {
  public static void main(java.lang.String[]);
}

#
-keep,includedescriptorclasses class entity.**$$serializer {*;}
#noinspection ShrinkerUnresolvedReference
-keepclassmembers class entity.** {
  *** Companion;
  <fields>;
  *** serializer();
}
#noinspection ShrinkerUnresolvedReference
-keepclasseswithmembers class entity.** {
  kotlinx.serialization.KSerializer $$serializer(...);
}

##
-keep,includedescriptorclasses class template.model.**$$serializer {*;}
#noinspection ShrinkerUnresolvedReference
-keepclassmembers class template.model.** {
  *** Companion;
  <fields>;
  *** serializer();
}
-keepclasseswithmembers class template.model.** {
  kotlinx.serialization.KSerializer $$serializer(...);
}