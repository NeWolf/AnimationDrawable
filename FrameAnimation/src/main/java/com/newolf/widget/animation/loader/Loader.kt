package com.newolf.widget.animation.loader

import com.newolf.widget.animation.io.Reader
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
interface Loader {
    @Throws(IOException::class)
    fun obtain(): Reader
}