package common.ext

val <T> T.exhaustive: T get() = this
val isART: Boolean by lazy(LazyThreadSafetyMode.NONE) {
    System.getProperty("java.vm.version", "")
        ?.split(".")
        ?.get(0)
        ?.toIntOrNull()?.let { it >= 2 } == true
}
