package com.mobeedev.library.dragstate

interface DragStateListener {
    fun onDragStart()

    fun onDragEnd(isMenuOpened: Boolean)
}