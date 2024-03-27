package com.newolf.widget.animation.decode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper


import androidx.annotation.WorkerThread
import com.newolf.widget.animation.Config
import com.newolf.widget.animation.executor.FrameDecoderExecutor
import com.newolf.widget.animation.io.Reader
import com.newolf.widget.animation.io.Writer
import com.newolf.widget.animation.loader.Loader
import com.newolf.widget.animation.utils.DebugLog
import java.io.IOException
import java.nio.ByteBuffer
import java.util.WeakHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.LockSupport
import kotlin.concurrent.Volatile
import kotlin.math.max
import kotlin.math.min

/**
 * ======================================================================
 * Abstract Frame Animation Decoder
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-03-26
 *
 * =======================================================================
 */
abstract class FrameSeqDecoder<R : Reader, W : Writer>(
    private val mLoader: Loader,
    renderListener: RenderListener?
) {
    companion object {
        const val TAG = "FrameSeqDecoder"

        val RECT_EMPTY = Rect()
    }

    private val renderListeners: MutableSet<RenderListener> = mutableSetOf()
    private val taskId by lazy {
        FrameDecoderExecutor.getInstance().generateTaskId()
    }
    private val workerHandler: Handler by lazy {
        Handler(FrameDecoderExecutor.getInstance().getLooper(taskId))
    }

    init {
        if (renderListener != null) {
            this.renderListeners.add(renderListener)
        }
        DebugLog.dTag(TAG, "taskId = $ taskId, workerHandler = $workerHandler")
    }

    fun addRenderListener(renderListener: RenderListener?) {
        workerHandler.post { renderListeners.remove(renderListener) }
    }

    fun removeRenderListener(renderListener: RenderListener?) {
        workerHandler.post { renderListeners.remove(renderListener) }
    }


    private var sampleSize = 1
    fun getSampleSize(): Int {
        return sampleSize
    }

    private val paused = AtomicBoolean(true)

    private var finished = false
    private var playCount: Int = -1
    private var frameIndex: Int = -1
    private fun canStep(): Boolean {
        if (!isRunning()) {
            return false
        }
        if (getFrameCount() == 0) {
            return false
        }
        if (getNumPlays() <= 0) {
            return true
        }
        if (this.playCount < getNumPlays() - 1) {
            return true
        } else if (this.playCount == getNumPlays() - 1 && this.frameIndex < this.getFrameCount() - 1) {
            return true
        }
        finished = true
        return false
    }

    private val loopLimit: Int? = null
    private fun getNumPlays(): Int {
        return this.loopLimit ?: this.getLoopCount()
    }

    protected abstract fun getLoopCount(): Int

    protected var frames: MutableList<Frame<R, W>> = mutableListOf<Frame<R, W>>()

    fun getFrameCount(): Int {
        return this.frames.size
    }

    private val renderTask: Runnable = object : Runnable {
        override fun run() {
            DebugLog.dTag(TAG, "$this is run")
            if (paused.get()) {
                return
            }
            if (canStep()) {
                val start = System.currentTimeMillis()
                val delay: Long = step()
                val cost = System.currentTimeMillis() - start
                workerHandler.postDelayed(this, max(0, delay - cost))
                for (listener in renderListeners) {
                    frameBuffer?.let { listener.onRender(it) }
                }
            } else {
                stop()
            }
        }
    }

    fun stop() {
        if (fullRect === RECT_EMPTY) {
            return
        }
        if (mState == State.FINISHING || mState == State.IDLE) {
            DebugLog.dTag(TAG, debugInfo() + "No need to stop")
            return
        }
        if (mState == State.INITIALIZING) {
            DebugLog.eTag(TAG, "${debugInfo()} Processing,wait for finish at $mState")

        }
        DebugLog.dTag(TAG, debugInfo() + " Set state to finishing")
        mState = State.FINISHING
        if (Looper.myLooper() == workerHandler.looper) {
            innerStop()
        } else {
            workerHandler.post { innerStop() }
        }
    }

    private val cacheBitmapsLock = Any()
    private val cacheBitmaps: MutableSet<Bitmap> = mutableSetOf()
    protected var cachedCanvas: WeakHashMap<Bitmap, Canvas> = WeakHashMap()

    @WorkerThread
    private fun innerStop() {
        workerHandler.removeCallbacks(renderTask)
        frames.clear()
        synchronized(cacheBitmapsLock) {
            for (bitmap in cacheBitmaps) {
                if (bitmap != null && !bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }
            cacheBitmaps.clear()
        }
        if (frameBuffer != null) {
            frameBuffer = null
        }
        cachedCanvas.clear()
        try {
            if (mReader != null) {
                mReader!!.close()
                mReader = null
            }
            if (mWriter != null) {
                mWriter.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        release()
        DebugLog.dTag(TAG, debugInfo() + " release and Set state to IDLE")
        mState = State.IDLE
        for (renderListener in renderListeners) {
            renderListener.onEnd()
        }
    }

    abstract fun release()


    private fun debugInfo(): String {
        return if (Config.IS_DEBUG) {
            String.format(
                "thread is %s, decoder is %s,state is %s",
                Thread.currentThread(),
                this@FrameSeqDecoder,
                mState.toString()
            )
        } else ""

    }

    @WorkerThread
    private fun step(): Long {
        frameIndex++
        if (frameIndex >= getFrameCount()) {
            frameIndex = 0
            playCount++
        }
        val frame: Frame<R, W> = getFrame(frameIndex) ?: return 0
        renderFrame(frame)
        return frame.frameDuration
    }

    abstract fun renderFrame(frame: Frame<R, W>)

    fun getFrame(index: Int): Frame<R, W>? {
        return if (index < 0 || index >= frames.size) {
            null
        } else frames[index]
    }

    fun setDesiredSize(width: Int, height: Int): Int {
        DebugLog.dTag(TAG, "width = $width, height = $height")
        val sample: Int = getDesiredSample(width, height)
        if (sample != getSampleSize()) {
            val tempRunning: Boolean = isRunning()
            workerHandler.removeCallbacks(renderTask)
            workerHandler.post(Runnable {
                innerStop()
                try {
                    sampleSize = sample
                    initCanvasBounds(read(getReader(mLoader.obtain())))
                    if (tempRunning) {
                        innerStart()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            })
        }
        return sample
    }

    @WorkerThread
    private fun innerStart() {
        paused.compareAndSet(true, false)
        val start = System.currentTimeMillis()
        try {
            if (getFrameCount() == 0) {
                try {
                    if (mReader == null) {
                        mReader = getReader(mLoader.obtain())
                    } else {
                        mReader!!.reset()
                    }
                    initCanvasBounds(read(mReader!!))
                } catch (e: Throwable) {
                    DebugLog.eTag(TAG, "innerStart has exception ", e)
                }
            }
        } finally {
            DebugLog.dTag(
                TAG,
                debugInfo() + " Set state to RUNNING,cost " + (System.currentTimeMillis() - start)
            )
            mState = State.RUNNING
        }
        if (getNumPlays() == 0 || !finished) {
            frameIndex = -1
            renderTask.run()
            for (renderListener in renderListeners) {
                renderListener.onStart()
            }
        } else {
            DebugLog.dTag(TAG, debugInfo() + " No need to started")
        }
    }

    fun isRunning(): Boolean {
        return mState == State.RUNNING || mState == State.INITIALIZING
    }

    protected fun getDesiredSample(desiredWidth: Int, desiredHeight: Int): Int {
        if (desiredWidth == 0 || desiredHeight == 0) {
            return 1
        }

        val radio: Int =
            min(getBounds().width() / desiredWidth, getBounds().height() / desiredHeight)

        var sample = 1
        while (sample * 2 <= radio) {
            sample *= 2
        }
        return sample
    }

    @Volatile
    protected var fullRect: Rect? = null

    @Volatile
    private var mState: State = State.IDLE


    private enum class State {
        IDLE,
        RUNNING,
        INITIALIZING,
        FINISHING
    }

    private var mReader: R? = null
    private val mWriter: W by lazy { getWriter() }

    abstract fun getWriter(): W
    abstract fun getReader(reader: Reader): R


    fun getBounds(): Rect {
        if (fullRect == null) {
            if (mState == State.FINISHING) {
                DebugLog.dTag(TAG, "In finishing,do not interrupt")
            }
            val thread = Thread.currentThread()
            workerHandler.post(Runnable {
                try {
                    if (fullRect == null) {
                        if (mReader == null) {
                            mReader = getReader(mLoader.obtain())
                        } else {
                            mReader?.reset()
                        }
                        initCanvasBounds(read(mReader!!))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    fullRect = RECT_EMPTY
                } finally {
                    LockSupport.unpark(thread)
                }
            })
            LockSupport.park(thread)
        }
        return fullRect ?: RECT_EMPTY
    }

    protected var frameBuffer: ByteBuffer? = null
    private fun initCanvasBounds(rect: Rect) {
        fullRect = rect
        frameBuffer =
            ByteBuffer.allocate((rect.width() * rect.height() / (getSampleSize() * getSampleSize()) + 1) * 4)

    }


    @Throws(IOException::class)
    protected abstract fun read(reader: R): Rect
    fun reset() {
        workerHandler.post {
            playCount = 0
            frameIndex = -1
            finished = false
        }
    }

    fun start() {
        if (fullRect === RECT_EMPTY) {
            return
        }
        if (mState == State.RUNNING || mState == State.INITIALIZING) {
            DebugLog.dTag(TAG, debugInfo() + " Already started")
            return
        }
        if (mState == State.FINISHING) {
            DebugLog.dTag(TAG, debugInfo() + " Processing,wait for finish at " + mState)
        }
        DebugLog.dTag(TAG, debugInfo() + "Set state to INITIALIZING")
        mState = State.INITIALIZING
        if (Looper.myLooper() == workerHandler.looper) {
            innerStart()
        } else {
            workerHandler.post { innerStart() }
        }
    }

    fun stopIfNeeded() {
        workerHandler.post {
            if (renderListeners.size == 0) {
                stop()
            }
        }
    }


}