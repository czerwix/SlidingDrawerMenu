package com.mobeedev.library.transform

import android.view.View
import com.mobeedev.library.util.SideMenuUtils

private const val START_TRANSLATION = 0f

class YTranslationTransformation(private val _endTranslation: Float) : BaseTransformation {

    override fun transform(dragProgress: Float, rootView: View) {
        rootView.translationY = SideMenuUtils.evaluate(dragProgress, START_TRANSLATION, _endTranslation)
    }
}