package com.mobeedev.library.transform

import android.view.View

interface BaseTransformation {
    fun transform(dragProgress: Float, rootView: View)
}