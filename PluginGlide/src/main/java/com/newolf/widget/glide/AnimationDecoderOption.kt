package com.newolf.widget.glide

import com.bumptech.glide.load.Option

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
object AnimationDecoderOption {
    val NO_ANIMATION_BOUNDS_MEASURE: Option<Boolean> = Option.memory(
        "com.github.NeWolf.widget.glide.AnimationDecoderOption.DISABLE_ANIMATION_BOUNDS_MEASURE",
        true
    )
    val DISABLE_ANIMATION_APNG_DECODER: Option<Boolean> = Option.memory(
        "com.github.NeWolf.widget.glide.AnimationDecoderOption.DISABLE_ANIMATION_APNG_DECODER",
        false
    );
}