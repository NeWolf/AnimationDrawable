package com.newolf.widget.animation.loader

import com.newolf.widget.animation.io.Reader
import com.newolf.widget.animation.io.StreamReader
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
abstract class StreamLoader():Loader {
    final override fun obtain(): Reader {
        return StreamReader(getInputStream())
    }

    @Throws(IOException::class)
    protected abstract fun getInputStream(): InputStream
}