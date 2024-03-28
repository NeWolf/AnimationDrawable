package com.newolf.widget.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.newolf.widget.animation.decode.FrameSeqDecoder
import com.newolf.widget.animation.io.StreamReader
import com.newolf.widget.animation.utils.DebugLog
import com.newolf.widget.drawable.apng.decoder.APNGParser
import java.io.ByteArrayOutputStream
import java.io.IOException
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
class StreamAnimationDecoder(private val byteBufferAnimationDecoder: ByteBufferAnimationDecoder) :
    ResourceDecoder<InputStream, FrameSeqDecoder<*, *>> {
        companion object{
            const val TAG = "StreamAnimationDecoder"
        }
    override fun handles(source: InputStream, options: Options): Boolean {
        val result = (!options.get(AnimationDecoderOption.DISABLE_ANIMATION_APNG_DECODER)!! && APNGParser.isAPNG(
            StreamReader(source)
        ))
        DebugLog.dTag(TAG,"handles:InputStream result = $result")
        return result
    }

    override fun decode(
        source: InputStream,
        width: Int,
        height: Int,
        options: Options
    ): Resource<FrameSeqDecoder<*, *>>? {
        val data: ByteArray = inputStreamToBytes(source) ?: return null
        val byteBuffer = ByteBuffer.wrap(data)
        return byteBufferAnimationDecoder.decode(byteBuffer, width, height, options)
    }


    private fun inputStreamToBytes(`is`: InputStream): ByteArray? {
        val bufferSize = 16384
        val buffer = ByteArrayOutputStream(bufferSize)
        try {
            var nRead: Int
            val data = ByteArray(bufferSize)
            while (`is`.read(data).also { nRead = it } != -1) {
                buffer.write(data, 0, nRead)
            }
            buffer.flush()
        } catch (e: IOException) {
            return null
        }
        return buffer.toByteArray()
    }
}