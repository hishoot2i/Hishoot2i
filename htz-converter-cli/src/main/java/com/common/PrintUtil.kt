package com.common

object PrintUtil {
    @JvmStatic
    fun hex4(num: Long): String = "%08x".format(num)
    // fun bChar(num: Byte): Char = num.toChar()
    //
    // fun hex1(num: Int): String = String.format("%02x", num)
    //
    // fun hex2(num: Int): String = String.format("%04x", num)
    //
    // fun hex4(num: Int): String = String.format("%04x", num)
    // fun hex(bytes: ByteArray): String = buildString {
    //     for (_byte in bytes) append(String.format("%02x", _byte and 0xff.toByte()))
    // }
    // fun indent(src: String): String = """	${src.trim { it <= ' ' }.replace("\n", "\n\t")}
    // """
}
