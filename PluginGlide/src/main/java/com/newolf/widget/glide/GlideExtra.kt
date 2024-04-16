package com.newolf.widget.glide

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.newolf.widget.animation.decode.FrameSeqDecoder
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * ======================================================================
 *
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-04-16
 *
 * =======================================================================
 */
fun Glide.registryAPNG() {
    val registry =  this.registry
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
        Bitmap::class.java, FrameBitmapTranscoder(this.bitmapPool)
    )
}