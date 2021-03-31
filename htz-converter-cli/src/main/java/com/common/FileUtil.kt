package com.common
//
// import java.io.Closeable
// import java.io.File
// import java.io.IOException
// import java.io.InputStream
// import java.io.RandomAccessFile
//
// object FileUtil {
//
//     fun loadAsRAF(`is`: InputStream, temp: File?): RandomAccessFile {
//         val raf = RandomAccessFile(temp, "rwd")
//         val buffer = ByteArray(2048)
//         var tmp: Int
//         while (`is`.read(buffer).also { tmp = it } != -1) {
//             raf.write(buffer, 0, tmp)
//         }
//         raf.seek(0)
//         return raf
//     }
//
//     fun loadAsRAF(pathname: String): RandomAccessFile {
//         return if (!File(pathname).exists()) {
//             throw IllegalStateException("File not found=$pathname")
//         } else RandomAccessFile(pathname, "r")
//     }
//
//     fun closeQuietly(io: Closeable?) {
//         if (io != null) {
//             try {
//                 io.close()
//             } catch (e: IOException) {
//                 e.printStackTrace()
//             }
//         }
//     }
// }
