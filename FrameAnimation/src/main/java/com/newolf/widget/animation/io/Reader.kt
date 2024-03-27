package com.newolf.widget.animation.io

import java.io.Closeable
import java.io.IOException
import java.io.InputStream

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
interface Reader: Closeable {


    @Throws(IOException::class)
    fun skip(total: Long): Long

    @Throws(IOException::class)
    fun peek(): Byte

    @Throws(IOException::class)
    fun reset()

    fun position(): Int

    @Throws(IOException::class)
    fun read(buffer: ByteArray?, start: Int, byteCount: Int): Int

    @Throws(IOException::class)
    fun available(): Int

    @Throws(IOException::class)
    fun toInputStream(): InputStream?


}