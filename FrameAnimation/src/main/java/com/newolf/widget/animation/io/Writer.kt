package com.newolf.widget.animation.io

import java.io.Closeable
import java.io.IOException

/**
 * ======================================================================
 *
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-03-26
 *
 * =======================================================================
 */
interface Writer: Closeable{
    fun reset(size: Int)

    fun putByte(b: Byte)

    fun putBytes(b: ByteArray?)

    fun position(): Int

    fun skip(length: Int)

    fun toByteArray(): ByteArray?

    @Throws(IOException::class)
    override fun close()
}