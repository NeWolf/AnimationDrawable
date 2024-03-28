package com.newolf.widget.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.newolf.widget.animation.decode.FrameSeqDecoder

/**
 * ======================================================================
 *
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-03-28
 *
 * =======================================================================
 */
class FrameBitmapTranscoder(private val bitmapPool: BitmapPool) :ResourceTranscoder<FrameSeqDecoder<*,*>,Bitmap> {
    override fun transcode(
        toTranscode: Resource<FrameSeqDecoder<*, *>>,
        options: Options
    ): Resource<Bitmap>? {
        val decoder = toTranscode.get()
        return try {
            val bitmap =  decoder.getFrameBitmap(0)
            BitmapResource.obtain(bitmap,bitmapPool)
        }catch (e:Exception){
            null
        }
    }
}