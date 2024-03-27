package com.newolf.widget.animation.loader

import android.content.Context
import androidx.annotation.RawRes
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
class ResourceStreamLoader(private val context: Context,@RawRes private val resId:Int):StreamLoader() {
    override fun getInputStream(): InputStream {
        return context.resources.openRawResource(resId)
    }
}