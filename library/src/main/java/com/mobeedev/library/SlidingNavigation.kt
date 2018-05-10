package com.mobeedev.library

interface SlidingNavigation {

    fun isMenuClosed(): Boolean

    fun isMenuOpened(): Boolean

    fun isMenuLocked(): Boolean

    fun getLayout(): SlidingNavigation

    fun closeMenu()

    fun closeMenu(animated: Boolean)

    fun openMenu()

    fun setMenuLocked(locked: Boolean)

    fun openMenu(animated: Boolean)
}