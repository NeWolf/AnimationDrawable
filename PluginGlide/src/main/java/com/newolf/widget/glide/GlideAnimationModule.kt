package com.newolf.widget.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.LibraryGlideModule
import com.newolf.widget.animation.decode.FrameSeqDecoder
import com.newolf.widget.animation.utils.DebugLog
import java.io.InputStream
import java.nio.ByteBuffer

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
@GlideModule
class GlideAnimationModule : LibraryGlideModule() {
    companion object {
        const val TAG = "GlideAnimationModule"
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        DebugLog.dTag(TAG, "registerComponents: ")
        super.registerComponents(context, glide, registry)
        val byteBufferAnimationDecoder = ByteBufferAnimationDecoder()
        val streamAnimationDecoder = StreamAnimationDecoder(byteBufferAnimationDecoder)
        registry.prepend(
            InputStream::class.java,
            FrameSeqDecoder::class.java, streamAnimationDecoder
        )
        registry.prepend(
            ByteBuffer::class.java,
            FrameSeqDecoder::class.java, byteBufferAnimationDecoder
        )
        registry.register(
            FrameSeqDecoder::class.java,
            Drawable::class.java, FrameDrawableTranscoder()
        )
        registry.register(
            FrameSeqDecoder::class.java,
            Bitmap::class.java, FrameBitmapTranscoder(glide.bitmapPool)
        )
    }
}