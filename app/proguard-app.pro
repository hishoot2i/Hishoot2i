#
-keepattributes SourceFile,LineNumberTable
#
-keep class io.reactivex.rxjava3.disposables.RunnableDisposable { <init>(...); }
#
-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
