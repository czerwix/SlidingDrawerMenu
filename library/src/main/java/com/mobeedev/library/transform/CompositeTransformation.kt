package com.mobeedev.library.transform

import android.view.View

class CompositeTransformation(private val _transformations: List<BaseTransformation>) : BaseTransformation {
    override fun transform(dragProgress: Float, rootView: View) = _transformations.forEach { it.transform(dragProgress, rootView) }
}