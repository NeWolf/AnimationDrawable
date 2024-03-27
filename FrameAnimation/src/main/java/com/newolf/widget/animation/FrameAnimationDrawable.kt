package com.newolf.widget.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.DrawFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.newolf.widget.animation.decode.FrameSeqDecoder
import com.newolf.widget.animation.decode.RenderListener
import com.newolf.widget.animation.loader.Loader
import com.newolf.widget.animation.utils.DebugLog
import java.lang.ref.WeakReference
import java.nio.ByteBuffer

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
abstract class FrameAnimationDrawable<Decoder : FrameSeqDecoder<*, *>> : Drawable,
    Animatable2Compat,
    RenderListener {
    companion object {
        const val TAG = "FrameAnimationDrawable"

        const val MSG_ANIMATION_START = 0xf1
        const val MSG_ANIMATION_END = 0xf2

    }

    private val frameSeqDecoder: Decoder
    private var bitmap: Bitmap? = null

    constructor(
        frameSeqDecoder: Decoder
    ) : super() {
        this.frameSeqDecoder = frameSeqDecoder
        paint.isAntiAlias = true
    }

    constructor(
        provider: Loader
    ) : super() {
        this.frameSeqDecoder = this.createFrameSeqDecoder(provider, this)
        paint.isAntiAlias = true
    }


    private val paint: Paint = Paint()
    private val drawFilter: DrawFilter = PaintFlagsDrawFilter(
        0,
        Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
    )
    private val matrix: Matrix = Matrix()


    //    ====================================Drawable===================================
    /**
     * Draw in its bounds (set via setBounds) respecting optional effects such
     * as alpha (set via setAlpha) and color filter (set via setColorFilter).
     *
     * @param canvas The canvas to draw into
     */
    override fun draw(canvas: Canvas) {
        if (bitmap == null || bitmap?.isRecycled == true) {
            return
        }
        canvas.setDrawFilter(drawFilter)
        canvas.drawBitmap(bitmap!!, matrix, paint)
    }


    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        DebugLog.dTag(TAG, "left = $left , top = $top , right = $right , bottom = $bottom")
        super.setBounds(left, top, right, bottom)
        val oldSampleSize = frameSeqDecoder.getSampleSize()
        val sampleSize = frameSeqDecoder.setDesiredSize(bounds.width(), bounds.height())
        matrix.setScale(
            1.0f * getBounds().width() * sampleSize / frameSeqDecoder.getBounds().width(),
            1.0f * getBounds().height() * sampleSize / frameSeqDecoder.getBounds().height()
        )
        if (sampleSize != oldSampleSize) {
            bitmap = Bitmap.createBitmap(
                frameSeqDecoder.getBounds().width() / sampleSize,
                frameSeqDecoder.getBounds().height() / sampleSize,
                Bitmap.Config.ARGB_8888
            )
        }
    }

    /**
     * Specify an alpha value for the drawable. 0 means fully transparent, and
     * 255 means fully opaque.
     */
    override fun setAlpha(alpha: Int) {
        DebugLog.dTag(TAG, "alpha = $alpha ")
        paint.alpha = alpha
    }

    /**
     * Specify an optional color filter for the drawable.
     *
     *
     * If a Drawable has a ColorFilter, each output pixel of the Drawable's
     * drawing contents will be modified by the color filter before it is
     * blended onto the render target of a Canvas.
     *
     *
     *
     * Pass `null` to remove any existing color filter.
     *
     *
     * **Note:** Setting a non-`null` color
     * filter disables [tint][.setTintList].
     *
     *
     * @param colorFilter The color filter to apply, or `null` to remove the
     * existing color filter
     */
    override fun setColorFilter(colorFilter: ColorFilter?) {
        DebugLog.dTag(TAG, "colorFilter = $colorFilter ")
        paint.colorFilter = colorFilter
    }

    /**
     * Return the opacity/transparency of this Drawable.  The returned value is
     * one of the abstract format constants in
     * [android.graphics.PixelFormat]:
     * [android.graphics.PixelFormat.UNKNOWN],
     * [android.graphics.PixelFormat.TRANSLUCENT],
     * [android.graphics.PixelFormat.TRANSPARENT], or
     * [android.graphics.PixelFormat.OPAQUE].
     *
     *
     * An OPAQUE drawable is one that draws all all content within its bounds, completely
     * covering anything behind the drawable. A TRANSPARENT drawable is one that draws nothing
     * within its bounds, allowing everything behind it to show through. A TRANSLUCENT drawable
     * is a drawable in any other state, where the drawable will draw some, but not all,
     * of the content within its bounds and at least some content behind the drawable will
     * be visible. If the visibility of the drawable's contents cannot be determined, the
     * safest/best return value is TRANSLUCENT.
     *
     *
     * Generally a Drawable should be as conservative as possible with the
     * value it returns.  For example, if it contains multiple child drawables
     * and only shows one of them at a time, if only one of the children is
     * TRANSLUCENT and the others are OPAQUE then TRANSLUCENT should be
     * returned.  You can use the method [.resolveOpacity] to perform a
     * standard reduction of two opacities to the appropriate single output.
     *
     *
     * Note that the returned value does not necessarily take into account a
     * custom alpha or color filter that has been applied by the client through
     * the [.setAlpha] or [.setColorFilter] methods. Some subclasses,
     * such as [BitmapDrawable], [ColorDrawable], and [GradientDrawable],
     * do account for the value of [.setAlpha], but the general behavior is dependent
     * upon the implementation of the subclass.
     *
     * @return int The opacity class of the Drawable.
     *
     * @see android.graphics.PixelFormat
     */
    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    private val obtainedCallbacks: HashSet<WeakReference<Callback>> = HashSet()
    override fun invalidateSelf() {
        super.invalidateSelf()
        val temp: Set<WeakReference<Callback>> =
            HashSet<WeakReference<Callback>>(obtainedCallbacks)
        for (ref in temp) {
            val callback = ref.get()
            if (callback != null && callback !== getCallback()) {
                callback.invalidateDrawable(this)
            }
        }
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        hookRecordCallbacks()
        if (autoPlay) {
            DebugLog.dTag(
                TAG,
                "$this,visible:$visible,restart:$restart"
            )
            if (visible) {
                if (!isRunning) {
                    innerStart()
                }
            } else if (isRunning) {
                innerStop()
            }
        }
        return super.setVisible(visible, restart)
    }

    private fun hookRecordCallbacks() {
        val lost: MutableList<WeakReference<Callback>> = java.util.ArrayList()
        val callback = callback
        var recorded = false
        val temp: Set<WeakReference<Callback>> = java.util.HashSet(obtainedCallbacks)
        for (ref in temp) {
            val cb = ref.get()
            if (cb == null) {
                lost.add(ref)
            } else {
                if (cb === callback) {
                    recorded = true
                } else {
                    cb.invalidateDrawable(this)
                }
            }
        }
        for (ref in lost) {
            obtainedCallbacks.remove(ref)
        }
        if (!recorded) {
            obtainedCallbacks.add(WeakReference<Callback>(callback))
        }
    }

    //    ====================================Drawable===================================


    private val animationCallbacks: HashSet<Animatable2Compat.AnimationCallback> = HashSet()

    private val uiHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_ANIMATION_START -> {
                    val callbacks: ArrayList<Animatable2Compat.AnimationCallback> =
                        ArrayList(animationCallbacks)
                    for (animationCallback in callbacks) {
                        animationCallback.onAnimationStart(this@FrameAnimationDrawable)
                    }
                }

                MSG_ANIMATION_END -> {
                    val callbacks: ArrayList<Animatable2Compat.AnimationCallback> =
                        ArrayList(animationCallbacks)
                    for (animationCallback in callbacks) {
                        animationCallback.onAnimationEnd(this@FrameAnimationDrawable)
                    }
                }
            }
        }
    }

    //    ====================================Animatable2Compat===================================

    /**
     * Starts the drawable's animation.
     */
    override fun start() {
        DebugLog.dTag(TAG, "$this,start")
        if (frameSeqDecoder.isRunning()) {
            frameSeqDecoder.stop()
        }
        frameSeqDecoder.reset()
        innerStart()
    }

    private var autoPlay = true

    private fun innerStart() {
        DebugLog.dTag(TAG, "$this,innerStart")
        frameSeqDecoder.addRenderListener(this)
        if (autoPlay) {
            frameSeqDecoder.start()
        } else {
            if (!frameSeqDecoder.isRunning()) {
                frameSeqDecoder.start()
            }
        }
    }

    /**
     * Stops the drawable's animation.
     */
    override fun stop() {
        innerStop()
    }

    private fun innerStop() {
        DebugLog.dTag(TAG, "$this,stop")
        frameSeqDecoder.removeRenderListener(this)
        if (autoPlay) {
            frameSeqDecoder.stop()
        } else {
            frameSeqDecoder.stopIfNeeded()
        }
    }

    /**
     * Indicates whether the animation is running.
     *
     * @return True if the animation is running, false otherwise.
     */
    override fun isRunning(): Boolean {
        return frameSeqDecoder.isRunning()
    }

    /**
     * Adds a callback to listen to the animation events.
     *
     * @param callback Callback to add.
     */
    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        animationCallbacks.add(callback)
    }

    /**
     * Removes the specified animation callback.
     *
     * @param callback Callback to remove.
     * @return `false` if callback didn't exist in the call back list, or `true` if
     * callback has been removed successfully.
     */
    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return animationCallbacks.remove(callback)
    }

    /**
     * Removes all existing animation callbacks.
     */
    override fun clearAnimationCallbacks() {
        animationCallbacks.clear()
    }



    //    ====================================Animatable2Compat===================================

    abstract fun createFrameSeqDecoder(provider: Loader, listener: RenderListener): Decoder


    //    ====================================RenderListener===================================
    /**
     * 播放开始
     */
    override fun onStart() {
        Message.obtain(uiHandler, MSG_ANIMATION_START).sendToTarget()
    }

    /**
     * 帧播放
     */
    override fun onRender(byteBuffer: ByteBuffer) {
        if (!isRunning) {
            return
        }
        if (bitmap == null || bitmap?.isRecycled == true) {
            bitmap = Bitmap.createBitmap(
                frameSeqDecoder.getBounds().width() / frameSeqDecoder.getSampleSize(),
                frameSeqDecoder.getBounds().height() / frameSeqDecoder.getSampleSize(),
                Bitmap.Config.ARGB_8888
            )
        }
        byteBuffer.rewind()
        if (byteBuffer.remaining() < (bitmap?.getByteCount() ?: 0)) {
            Log.e(TAG, "onRender:Buffer not large enough for pixels")
            return
        }
        bitmap!!.copyPixelsFromBuffer(byteBuffer)
        uiHandler.post(invalidateRunnable)
    }

    private val invalidateRunnable = Runnable { invalidateSelf() }

    /**
     * 播放结束
     */
    override fun onEnd() {
        Message.obtain(uiHandler, MSG_ANIMATION_END).sendToTarget()
    }

    //    ====================================RenderListener===================================
}