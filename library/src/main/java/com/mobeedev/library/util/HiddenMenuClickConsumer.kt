package com.mobeedev.library.util

import android.content.Context
import android.view.MotionEvent
import android.view.View
import com.mobeedev.library.SlidingMenuLayout

class HiddenMenuClickConsumer(private var _menuHost: SlidingMenuLayout, context: Context) : View(context) {

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return _menuHost.isMenuClosed()
    }
}