package com.newolf.widget.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.newolf.widget.animation.decode.FrameSeqDecoder
import com.newolf.widget.animation.io.ByteBufferReader
import com.newolf.widget.animation.loader.ByteBufferLoader
import com.newolf.widget.animation.loader.Loader
import com.newolf.widget.animation.utils.DebugLog
import com.newolf.widget.drawable.apng.decoder.APNGDecoder
import com.newolf.widget.drawable.apng.decoder.APNGParser
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
class ByteBufferAnimationDecoder:ResourceDecoder<ByteBuffer, FrameSeqDecoder<*,*>> {
    companion object{
        const val TAG = "ByteBufferAnimationDecoder"
    }
    override fun handles(source: ByteBuffer, options: Options): Boolean {
        val result = (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_APNG_DECODER)!! && APNGParser.isAPNG(
            ByteBufferReader(source)) )
        DebugLog.dTag(TAG,"handles ByteBuffer result = $result")
        return result

    }

    override fun decode(
        source: ByteBuffer,
        width: Int,
        height: Int,
        options: Options
    ): Resource<FrameSeqDecoder<*, *>>? {
        val loader: Loader = object : ByteBufferLoader() {
            override fun getByteBuffer(): ByteBuffer {
                source.position(0)
                return source
            }

        }
        val decoder: APNGDecoder
       if (APNGParser.isAPNG(ByteBufferReader(source))) {
            decoder = APNGDecoder(loader, null)
        } else {
            return null
        }
        return FrameSeqDecoderResource(decoder,source.limit())

    }


    private class FrameSeqDecoderResource internal constructor(
        decoder: FrameSeqDecoder<*,*>,
        size: Int
    ) :
        Resource<FrameSeqDecoder<*,*>> {
        private val decoder: FrameSeqDecoder<*,*>
        private val size: Int

        init {
            this.decoder = decoder
            this.size = size
        }

        override fun getResourceClass(): Class<FrameSeqDecoder<*,*>> {
            return FrameSeqDecoder::class.java
        }

        override fun get(): FrameSeqDecoder<*,*> {
            return decoder
        }

        override fun getSize(): Int {
            return size
        }

        override fun recycle() {
            decoder.stop()
        }
    }

}