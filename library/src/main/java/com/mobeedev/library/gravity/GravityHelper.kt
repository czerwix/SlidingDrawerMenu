package com.mobeedev.library.gravity

import android.support.v4.widget.ViewDragHelper

sealed class GravityHelper {

    abstract fun getLeftAfterFling(flingVelocity: Float, maxDrag: Int): Int

    abstract fun getLeftToSettle(dragProgress: Float, maxDrag: Int): Int

    abstract fun getRootLeft(dragProgress: Float, maxDrag: Int): Int

    abstract fun getDragProgress(viewLeft: Int, maxDrag: Int): Float

    abstract fun clampViewPosition(left: Int, maxDrag: Int): Int

    abstract fun enableEdgeTrackingOn(dragHelper: ViewDragHelper)
}

object RightGravityHelper : GravityHelper() {

    override fun getLeftAfterFling(flingVelocity: Float, maxDrag: Int): Int {
        return if (flingVelocity > 0) 0 else -maxDrag
    }

    override fun getLeftToSettle(dragProgress: Float, maxDrag: Int): Int {
        return if (dragProgress > 0.5f) -maxDrag else 0
    }

    override fun getRootLeft(dragProgress: Float, maxDrag: Int): Int {
        return (-(dragProgress * maxDrag)).toInt()
    }

    override fun getDragProgress(viewLeft: Int, maxDrag: Int): Float {
        return Math.abs(viewLeft).toFloat() / maxDrag
    }

    override fun clampViewPosition(left: Int, maxDrag: Int): Int {
        return Math.max(-maxDrag, Math.min(left, 0))
    }

    override fun enableEdgeTrackingOn(dragHelper: ViewDragHelper) {
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT)
    }
}

object LeftGravityHelper : GravityHelper() {

    override fun getLeftAfterFling(flingVelocity: Float, maxDrag: Int): Int {
        return if (flingVelocity > 0) maxDrag else 0
    }

    override fun getLeftToSettle(dragProgress: Float, maxDrag: Int): Int {
        return if (dragProgress > 0.5f) maxDrag else 0
    }

    override fun getRootLeft(dragProgress: Float, maxDrag: Int): Int {
        return (dragProgress * maxDrag).toInt()
    }

    override fun getDragProgress(viewLeft: Int, maxDrag: Int): Float {
        return viewLeft.toFloat() / maxDrag
    }

    override fun clampViewPosition(left: Int, maxDrag: Int): Int {
        return Math.max(0, Math.min(left, maxDrag))
    }

    override fun enableEdgeTrackingOn(dragHelper: ViewDragHelper) {
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT)
    }
}