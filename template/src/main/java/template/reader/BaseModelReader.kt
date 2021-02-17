package template.reader

import androidx.annotation.WorkerThread
import java.io.InputStream
import java.io.InputStreamReader

@WorkerThread
abstract class BaseModelReader<out T>(
    inputStream: InputStream
) : InputStreamReader(inputStream, Charsets.UTF_8) {
    @Throws(Exception::class)
    abstract fun model(): T
}
