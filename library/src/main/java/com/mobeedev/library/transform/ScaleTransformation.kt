package com.mobeedev.library.transform

import android.view.View
import com.mobeedev.library.util.SideMenuUtils

private const val START_SCALE = 1f

class ScaleTransformation(private val _endScale: Float) : BaseTransformation {

    override fun transform(dragProgress: Float, rootView: View) {
        val scale = SideMenuUtils.evaluate(dragProgress, START_SCALE, _endScale)
        rootView.scaleX = scale
        rootView.scaleY = scale
    }
}