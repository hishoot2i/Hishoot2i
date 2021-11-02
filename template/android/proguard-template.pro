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
# nav arg
-keep class template.TemplateComparator
