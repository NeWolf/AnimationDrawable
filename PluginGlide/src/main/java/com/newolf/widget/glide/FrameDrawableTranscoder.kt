package com.newolf.widget.glide

import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.drawable.DrawableResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.newolf.widget.animation.decode.FrameSeqDecoder
import com.newolf.widget.animation.utils.DebugLog
import com.newolf.widget.drawable.apng.APNGDrawable
import com.newolf.widget.drawable.apng.decoder.APNGDecoder

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
class FrameDrawableTranscoder:ResourceTranscoder<FrameSeqDecoder<*,*>,Drawable> {
    companion object{
        const val TAG = "FrameDrawableTranscoder"
    }
    override fun transcode(
        toTranscode: Resource<FrameSeqDecoder<*, *>>,
        options: Options
    ): DrawableResource<Drawable>? {
       val decoder =  toTranscode.get()
        val noMeasure = options.get<Boolean>(AnimationDecoderOption.NO_ANIMATION_BOUNDS_MEASURE)!!
        DebugLog.dTag(TAG,"transcode: noMeasure = $noMeasure")
        if (decoder is APNGDecoder) {
            val apngDrawable = (decoder as APNGDecoder?)?.let { APNGDrawable(it) }
            apngDrawable?.setAutoPlay(false)
            apngDrawable?.setNoMeasure(noMeasure)

            return object :DrawableResource<Drawable>(apngDrawable){
                override fun getResourceClass(): Class<Drawable> {
                    return Drawable::class.java
                }

                override fun getSize(): Int {
                   return apngDrawable?.getMemorySize() ?: 0
                }

                override fun recycle() {
                    apngDrawable?.stop()
                }

                override fun initialize() {
                    super.initialize()
                    apngDrawable?.setAutoPlay(true)
                }
            }
        }else {
            return null
        }
    }
}