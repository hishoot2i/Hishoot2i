@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.manifest.data

import java.util.Locale

object AttributeType {
    const val TYPE_NULL = 0x00
    const val TYPE_REFERENCE = 0x01
    const val TYPE_ATTRIBUTE = 0x02
    const val TYPE_STRING = 0x03
    const val TYPE_FLOAT = 0x04
    const val TYPE_DIMENSION = 0x05
    const val TYPE_FRACTION = 0x06
    const val TYPE_DYNAMIC_REFERENCE = 0x07
    const val TYPE_FIRST_INT = 0x10 // Beginning of integer flavors...
    const val TYPE_INT_DEC = 0x10 // n..n.
    const val TYPE_INT_HEX = 0x11 // 0xn..n.
    const val TYPE_INT_BOOLEAN = 0x12 // 0 or 1, "false" or "true"
    const val TYPE_FIRST_COLOR_INT = 0x1c // Beginning of color integer flavors...
    const val TYPE_INT_COLOR_ARGB8 = 0x1c // #aarrggbb.
    const val TYPE_INT_COLOR_RGB8 = 0x1d // #rrggbb.
    const val TYPE_INT_COLOR_ARGB4 = 0x1e // #argb.
    const val TYPE_INT_COLOR_RGB4 = 0x1f // ##rgb.
    const val TYPE_LAST_COLOR_INT = 0x1f // ..end of integer flavors.
    const val TYPE_LAST_INT = 0x1f // ...end of integer flavors.
    const val COMPLEX_UNIT_SHIFT = 0
    const val COMPLEX_UNIT_MASK = 0xf
    const val COMPLEX_UNIT_PX = 0
    const val COMPLEX_UNIT_DIP = 1
    const val COMPLEX_UNIT_SP = 2
    const val COMPLEX_UNIT_PT = 3
    const val COMPLEX_UNIT_IN = 4
    const val COMPLEX_UNIT_MM = 5
    const val COMPLEX_UNIT_FRACTION = 6
    const val COMPLEX_UNIT_FRACTION_PARENT = 7

    fun getAttributeType(entry: AttributeEntry): String = when (entry.type) {
        TYPE_REFERENCE.toLong() -> "TYPE_REFERENCE"
        TYPE_ATTRIBUTE.toLong() -> "TYPE_ATTRIBUTE"
        TYPE_STRING.toLong() -> "TYPE_STRING"
        TYPE_FLOAT.toLong() -> "TYPE_FLOAT"
        TYPE_DIMENSION.toLong() -> "TYPE_DIMENSION"
        TYPE_FRACTION.toLong() -> "TYPE_FRACTION"
        TYPE_DYNAMIC_REFERENCE.toLong() -> "TYPE_DYNAMIC_REFERENCE"
        TYPE_INT_DEC.toLong() -> "TYPE_INT_DEC"
        TYPE_INT_HEX.toLong() -> "TYPE_INT_HEX"
        TYPE_INT_BOOLEAN.toLong() -> "TYPE_INT_BOOLEAN"
        TYPE_INT_COLOR_ARGB8.toLong() -> "TYPE_INT_COLOR_ARGB8"
        TYPE_INT_COLOR_RGB8.toLong() -> "TYPE_INT_COLOR_RGB8"
        TYPE_INT_COLOR_ARGB4.toLong() -> "TYPE_INT_COLOR_ARGB4"
        TYPE_INT_COLOR_RGB4.toLong() -> "TYPE_INT_COLOR_RGB4"
        else -> "TYPE_UNKNOWN"
    }

    fun getAttributeData(entry: AttributeEntry, stringChunk: StringChunk): String =
        when (entry.type) {
            TYPE_REFERENCE.toLong() -> "@%s/0x%08x".format(getPackage(entry.data), entry.data)
            TYPE_ATTRIBUTE.toLong() -> "?%s/0x%08x".format(getPackage(entry.data), entry.data)
            TYPE_STRING.toLong() -> stringChunk.getString(entry.data) ?: "Unknown"
            TYPE_FLOAT.toLong() -> java.lang.Float.intBitsToFloat(entry.data.toInt()).toString()
            TYPE_DIMENSION.toLong() -> java.lang.Float.intBitsToFloat(entry.data.toInt())
                .toString() + getDimenUnit(entry.data)
            TYPE_FRACTION.toLong() -> java.lang.Float.intBitsToFloat(entry.data.toInt())
                .toString() + getFractionUnit(entry.data)
            TYPE_DYNAMIC_REFERENCE.toLong() -> "TYPE_DYNAMIC_REFERENCE"
            TYPE_INT_DEC.toLong() -> "%d".format(Locale.ROOT, entry.data)
            TYPE_INT_HEX.toLong() -> "0x%08x".format(entry.data)
            TYPE_INT_BOOLEAN.toLong() -> if (entry.data == 0L) "false" else "true"
            TYPE_INT_COLOR_ARGB8.toLong() -> "#%08x".format(entry.data)
            TYPE_INT_COLOR_RGB8.toLong() -> "#ff%06x".format(0xffffff and entry.data.toInt())
            TYPE_INT_COLOR_ARGB4.toLong() -> "#%04x".format(0xffff and entry.data.toInt())
            TYPE_INT_COLOR_RGB4.toLong() -> "#f%03x".format(0x0fff and entry.data.toInt())
            else -> "<0x%08x, type 0x%08x>".format(entry.data, entry.type)
        }

    private fun getDimenUnit(data: Long): String =
        when ((data shr COMPLEX_UNIT_SHIFT and COMPLEX_UNIT_MASK.toLong()).toInt()) {
            COMPLEX_UNIT_PX -> "px"
            COMPLEX_UNIT_DIP -> "dp"
            COMPLEX_UNIT_SP -> "sp"
            COMPLEX_UNIT_PT -> "pt"
            COMPLEX_UNIT_IN -> "in"
            COMPLEX_UNIT_MM -> "mm"
            else -> " (unknown unit)"
        }

    private fun getFractionUnit(data: Long): String =
        when ((data shr COMPLEX_UNIT_SHIFT and COMPLEX_UNIT_MASK.toLong()).toInt()) {
            COMPLEX_UNIT_FRACTION -> "%%"
            COMPLEX_UNIT_FRACTION_PARENT -> "%%p"
            else -> " (unknown unit)"
        }

    private fun getPackage(@Suppress("UNUSED_PARAMETER") data: Long): String = "ref"
}
