package com.mobeedev.library.transform

import android.os.Build
import android.view.View
import com.mobeedev.library.util.SideMenuUtils

private const val START_ELEVATION = 0f

class ElevationTransformation(private val _endElevation: Float) : BaseTransformation {

    override fun transform(dragProgress: Float, rootView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) rootView.elevation = SideMenuUtils.evaluate(dragProgress, START_ELEVATION, _endElevation)
    }
}