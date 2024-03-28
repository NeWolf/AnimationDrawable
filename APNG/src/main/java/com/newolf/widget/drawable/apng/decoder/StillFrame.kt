package com.newolf.widget.drawable.apng.decoder

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import com.newolf.widget.animation.decode.Frame
import com.newolf.widget.animation.utils.DebugLog
import com.newolf.widget.drawable.apng.io.APNGReader
import com.newolf.widget.drawable.apng.io.APNGWriter
import java.io.IOException

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
class StillFrame(reader: APNGReader) : Frame<APNGReader, APNGWriter>(reader) {
    companion object {
        const val TAG = "StillFrame"
    }

    override fun draw(
        canvas: Canvas?,
        paint: Paint?,
        sampleSize: Int,
        reusedBitmap: Bitmap?,
        writer: APNGWriter
    ): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        options.inSampleSize = sampleSize
        options.inMutable = true
        options.inBitmap = reusedBitmap
        var bitmap: Bitmap? = null
        try {
            reader.reset()
            try {
                bitmap = BitmapFactory.decodeStream(reader.toInputStream(), null, options)
            } catch (e: IllegalArgumentException) {
                // Problem decoding into existing bitmap when on Android 4.2.2 & 4.3
                val optionsFixed = BitmapFactory.Options()
                optionsFixed.inJustDecodeBounds = false
                optionsFixed.inSampleSize = sampleSize
                optionsFixed.inMutable = true
                bitmap = BitmapFactory.decodeStream(reader.toInputStream(), null, optionsFixed)
            }
            assert(bitmap != null)
            paint?.setXfermode(null)
            canvas?.drawBitmap(bitmap!!, 0f, 0f, paint)
        } catch (e: IOException) {
            DebugLog.eTag(TAG,"draw has IOException ",e)
        }
        return bitmap
    }


}