import java.io.InputStream

object TestHelper {
    fun resourceStreamOrThrow(fileName: String): InputStream =
        TestHelper.javaClass.classLoader.getResourceAsStream(fileName)
            ?: throw IllegalStateException("Can't load $fileName")
}
