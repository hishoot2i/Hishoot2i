@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import java.io.File

inline fun File.compareByName(other: File): Int = if (name == other.name) 0
else name.compareTo(other.name, true)

inline fun File.isDirAndCanRead(): Boolean = isDirectory && canRead()

@JvmOverloads
inline fun File.listFilesOrEmpty(
    crossinline filter: (File) -> Boolean = { true }
): Array<File> = listFiles { file: File -> filter(file) } ?: emptyArray()

inline fun File.listFilesByExt(
    vararg extensions: String
): Array<File> = listFilesOrEmpty { file: File -> file.extension in extensions }
