package template.reader

import android.util.JsonReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.LazyThreadSafetyMode.NONE

abstract class AbsJsonModelReader<out T>(
    inputStream: InputStream
) : InputStreamReader(inputStream, Charsets.UTF_8) {
    protected val jsonReader by lazy(NONE) { JsonReader(this) }
    @Throws(Exception::class)
    abstract fun model(): T
}
