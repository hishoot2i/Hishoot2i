package com.manifest.stream

import com.common.stream.RandomAccessStreamer

abstract class MfStreamer : RandomAccessStreamer() {
    abstract fun readUInt(): Long
    abstract fun readUShort(): Int

    // utf-8 char
    abstract fun readChar8(): Char

    // utf-16 char
    abstract fun readChar16(): Char
}
