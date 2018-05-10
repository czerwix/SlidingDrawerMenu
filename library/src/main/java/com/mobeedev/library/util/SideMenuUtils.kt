package com.mobeedev.library.util

object SideMenuUtils {
    fun evaluate(fraction: Float, startValue: Float, endValue: Float) = startValue + fraction * (endValue - startValue)
}