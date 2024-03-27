package com.newolf.widget.animation.loader

import android.content.Context
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
class AssetStreamLoader(private val context: Context, private val assetName: String) :
    StreamLoader() {
    override fun getInputStream(): InputStream {
        return context.assets.open(assetName)
    }

}