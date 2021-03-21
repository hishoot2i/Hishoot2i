#
-keep,includedescriptorclasses class entity.**$$serializer {*;}
#noinspection ShrinkerUnresolvedReference
-keepclassmembers class entity.** {
  *** Companion;
  <fields>;
  *** serializer();
}
-keepclasseswithmembers class entity.** {
  kotlinx.serialization.KSerializer $$serializer(...);
}
