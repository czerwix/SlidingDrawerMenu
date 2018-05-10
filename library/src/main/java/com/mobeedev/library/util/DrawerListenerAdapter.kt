package com.mobeedev.library.util

import android.support.v4.widget.DrawerLayout
import android.view.View

import com.mobeedev.library.dragstate.DragListener
import com.mobeedev.library.dragstate.DragStateListener


class DrawerListenerAdapter(private val _adaptee: DrawerLayout.DrawerListener, private val _drawer: View) : DragListener, DragStateListener {

    override fun onDrag(progress: Float) = _adaptee.onDrawerSlide(_drawer, progress)

    override fun onDragStart() = _adaptee.onDrawerStateChanged(DrawerLayout.STATE_DRAGGING)

    override fun onDragEnd(isMenuOpened: Boolean) {
        if (isMenuOpened) {
            _adaptee.onDrawerOpened(_drawer)
        } else {
            _adaptee.onDrawerClosed(_drawer)
        }
        _adaptee.onDrawerStateChanged(DrawerLayout.STATE_IDLE)
    }
}