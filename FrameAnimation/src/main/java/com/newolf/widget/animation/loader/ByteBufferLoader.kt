package com.newolf.widget.animation.loader

import com.newolf.widget.animation.io.ByteBufferReader
import java.io.IOException
import java.nio.ByteBuffer

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
abstract class ByteBufferLoader :Loader {
    abstract fun getByteBuffer(): ByteBuffer

    @Throws(IOException::class)
    override fun obtain(): ByteBufferReader {
        return ByteBufferReader(getByteBuffer())
    }
}