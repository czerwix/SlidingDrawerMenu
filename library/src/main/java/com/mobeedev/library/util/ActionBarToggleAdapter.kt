package com.mobeedev.library.util

import android.content.Context
import android.support.v4.widget.DrawerLayout
import com.mobeedev.library.SlidingNavigation

class ActionBarToggleAdapter(private var _adapter: SlidingNavigation, context: Context) : DrawerLayout(context) {

    override fun openDrawer(gravity: Int) = _adapter.openMenu()

    override fun closeDrawer(gravity: Int) = _adapter.closeMenu()

    override fun isDrawerVisible(drawerGravity: Int) = !_adapter.isMenuClosed()

    override fun getDrawerLockMode(edgeGravity: Int): Int = when {
        _adapter.isMenuLocked() && _adapter.isMenuClosed() -> DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        _adapter.isMenuLocked() && !_adapter.isMenuClosed() -> DrawerLayout.LOCK_MODE_LOCKED_OPEN
        else -> DrawerLayout.LOCK_MODE_UNLOCKED
    }
}