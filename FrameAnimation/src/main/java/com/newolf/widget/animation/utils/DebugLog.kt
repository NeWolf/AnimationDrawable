package com.newolf.widget.animation.utils

import android.util.Log
import com.newolf.widget.animation.Config
import java.lang.Exception

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
object DebugLog {
    private const val TAG = "Wolf_Animation"

    fun d(msg: String) {
        dTag(TAG, msg)
    }

    fun dTag(tag: String, msg: String) {
        if (Config.IS_DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun e(msg: String, t: Throwable? = null) {
        eTag(TAG, msg, t)
    }


    fun eTag(tag: String, msg: String, t: Throwable? = null) {
        if (Config.IS_DEBUG) {
            Log.e(tag, msg, t)
        }
    }


}