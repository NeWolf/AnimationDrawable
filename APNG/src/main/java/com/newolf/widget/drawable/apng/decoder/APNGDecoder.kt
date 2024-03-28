package com.newolf.widget.drawable.apng.decoder

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import com.newolf.widget.animation.decode.Frame
import com.newolf.widget.animation.decode.FrameSeqDecoder
import com.newolf.widget.animation.decode.RenderListener
import com.newolf.widget.animation.io.Reader
import com.newolf.widget.animation.loader.Loader
import com.newolf.widget.animation.utils.DebugLog
import com.newolf.widget.drawable.apng.io.APNGReader
import com.newolf.widget.drawable.apng.io.APNGWriter
import java.nio.ByteBuffer

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
class APNGDecoder(
    mLoader: Loader,
    renderListener: RenderListener?
) : FrameSeqDecoder<APNGReader, APNGWriter>(mLoader, renderListener) {

    companion object {
        const val TAG = "APNGDecoder"

    }

    private var apngWriter: APNGWriter? = null
    private var mLoopCount = 0
    private val paint = Paint()


    private class SnapShot {
        var dispose_op: Byte = 0
        var dstRect = Rect()
        var byteBuffer: ByteBuffer? = null
    }


    private val snapShot = SnapShot()


    override fun getWriter(): APNGWriter {
        if (apngWriter == null) {
            apngWriter = APNGWriter()
        }
        return apngWriter!!
    }

    override fun getReader(reader: Reader): APNGReader {
        return APNGReader(reader)
    }

    override fun read(reader: APNGReader): Rect {
        val chunks: List<Chunk> = APNGParser.parse(reader)
        val otherChunks: MutableList<Chunk> = java.util.ArrayList<Chunk>()

        var actl = false
        var lastFrame: APNGFrame? = null
        var ihdrData = ByteArray(0)
        var canvasWidth = 0
        var canvasHeight = 0
        for (chunk in chunks) {
            if (chunk is IENDChunk) {
                DebugLog.dTag(TAG, "chunk read reach to end")
                break

            }
            if (chunk is ACTLChunk) {
                mLoopCount = chunk.num_plays
                actl = true
            } else if (chunk is FCTLChunk) {
                val frame = APNGFrame(reader, chunk)
                frame.prefixChunks = otherChunks
                frame.ihdrData = ihdrData
                frames.add(frame)
                lastFrame = frame
            } else if (chunk is FDATChunk) {
                lastFrame?.imageChunks?.add(chunk)
            } else if (chunk is IDATChunk) {
                if (!actl) {
                    //如果为非APNG图片，则只解码PNG
                    val frame: Frame<APNGReader, APNGWriter> = StillFrame(reader)
                    frame.frameWidth = canvasWidth
                    frame.frameHeight = canvasHeight
                    frames.add(frame)
                    mLoopCount = 1
                    break
                }
                lastFrame?.imageChunks?.add(chunk)
            } else if (chunk is IHDRChunk) {
                canvasWidth = chunk.width
                canvasHeight = chunk.height
                ihdrData = chunk.data
            } else if (chunk !is IENDChunk) {
                otherChunks.add(chunk)
            }
        }
        frameBuffer =
            ByteBuffer.allocate((canvasWidth * canvasHeight / (sampleSize * sampleSize) + 1) * 4)
        snapShot.byteBuffer =
            ByteBuffer.allocate((canvasWidth * canvasHeight / (sampleSize * sampleSize) + 1) * 4)
        return Rect(0, 0, canvasWidth, canvasHeight)
    }

    override fun getLoopCount(): Int {
        return mLoopCount
    }

    override fun release() {
        snapShot.byteBuffer = null
        apngWriter = null
    }


    override fun renderFrame(frame: Frame<APNGReader, APNGWriter>) {
        if (fullRect == null) {
            return
        }
        try {
            val bitmap: Bitmap? =
                obtainBitmap(fullRect!!.width() / sampleSize, fullRect!!.height() / sampleSize)
            var canvas = cachedCanvas[bitmap]
            if (canvas == null) {
                canvas = bitmap?.let { Canvas(it) }
                cachedCanvas[bitmap] = canvas
            }
            if (frame is APNGFrame) {
                // 从缓存中恢复当前帧
                frameBuffer!!.rewind()
                bitmap?.copyPixelsFromBuffer(frameBuffer!!)
                // 开始绘制前，处理快照中的设定
                if (frameIndex == 0) {
                    canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                } else {
                    canvas?.save()
                    canvas?.clipRect(snapShot.dstRect)
                    when (snapShot.dispose_op) {
                        FCTLChunk.APNG_DISPOSE_OP_PREVIOUS.toByte() -> {
                            snapShot.byteBuffer!!.rewind()
                            bitmap?.copyPixelsFromBuffer(snapShot.byteBuffer!!)
                        }

                        FCTLChunk.APNG_DISPOSE_OP_BACKGROUND.toByte() -> canvas?.drawColor(
                            Color.TRANSPARENT,
                            PorterDuff.Mode.CLEAR
                        )

                        FCTLChunk.APNG_DISPOSE_OP_NON.toByte() -> {}
                        else -> {}
                    }
                    canvas?.restore()
                }

                // 然后根据dispose设定传递到快照信息中
                if (frame.dispose_op.toInt() == FCTLChunk.APNG_DISPOSE_OP_PREVIOUS) {
                    if (snapShot.dispose_op.toInt() != FCTLChunk.APNG_DISPOSE_OP_PREVIOUS) {
                        snapShot.byteBuffer!!.rewind()
                        bitmap?.copyPixelsToBuffer(snapShot.byteBuffer!!)
                    }
                }
                snapShot.dispose_op = frame.dispose_op
                canvas?.save()
                if ((frame as APNGFrame?)?.blend_op?.toInt() == FCTLChunk.APNG_BLEND_OP_SOURCE) {
                    canvas?.clipRect(
                        frame.frameX / sampleSize,
                        frame.frameY / sampleSize,
                        (frame.frameX + frame.frameWidth) / sampleSize,
                        (frame.frameY + frame.frameHeight) / sampleSize
                    )
                    canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                }
                snapShot.dstRect[frame.frameX / sampleSize, frame.frameY / sampleSize, (frame.frameX + frame.frameWidth) / sampleSize] =
                    (frame.frameY + frame.frameHeight) / sampleSize
                canvas?.restore()
            }
            //开始真正绘制当前帧的内容
            val inBitmap: Bitmap? = obtainBitmap(frame.frameWidth, frame.frameHeight)
            recycleBitmap(frame.draw(canvas, paint, sampleSize, inBitmap, getWriter()))
            recycleBitmap(inBitmap)
            frameBuffer?.rewind()
            bitmap?.copyPixelsToBuffer(frameBuffer!!)
            recycleBitmap(bitmap)
        } catch (e: Exception) {
            DebugLog.eTag(TAG, "renderFrame has Exception", e)
        }
    }
}