package com.newolf.widget.drawable.apng

import android.content.Context
import androidx.annotation.RawRes
import com.newolf.widget.animation.FrameAnimationDrawable
import com.newolf.widget.animation.decode.RenderListener
import com.newolf.widget.animation.loader.AssetStreamLoader
import com.newolf.widget.animation.loader.FileLoader
import com.newolf.widget.animation.loader.Loader
import com.newolf.widget.animation.loader.ResourceStreamLoader
import com.newolf.widget.drawable.apng.decoder.APNGDecoder

/**
 * ======================================================================
 *
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-03-27
 *
 * =======================================================================
 */
class APNGDrawable :
    FrameAnimationDrawable<APNGDecoder> {


    companion object {
        fun fromAsset(context: Context, assetName: String): APNGDrawable {
            val assetStreamLoader = AssetStreamLoader(context, assetName)
            return APNGDrawable(assetStreamLoader)
        }

        fun fromResource(context: Context, @RawRes resId: Int): APNGDrawable {
            val resourceStreamLoader = ResourceStreamLoader(context, resId)
            return APNGDrawable(resourceStreamLoader)
        }

        fun fromFile(filePath: String): APNGDrawable {
            val fileLoader = FileLoader(filePath)
            return APNGDrawable(fileLoader)
        }


    }

    constructor(loader: Loader) : super(loader)
    constructor(decoder: APNGDecoder) : super(decoder)


    override fun createFrameSeqDecoder(provider: Loader, listener: RenderListener): APNGDecoder {
        return APNGDecoder(provider, listener)
    }


}