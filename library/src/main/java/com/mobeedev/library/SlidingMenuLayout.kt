package com.mobeedev.library

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import com.mobeedev.library.SlidingNavigation
import com.mobeedev.library.dragstate.DragListener
import com.mobeedev.library.dragstate.DragStateListener
import com.mobeedev.library.gravity.GravityHelper
import com.mobeedev.library.gravity.SlideGravity
import com.mobeedev.library.transform.BaseTransformation

private const val EXTRA_IS_OPENED = "menu_extra_is_opened"
private const val EXTRA_SUPER = "menu_extra_super"
private const val EXTRA_SHOULD_BLOCK_CLICK = "menu_extra_should_block_click"

class SlidingMenuLayout(context: Context) : FrameLayout(context), SlidingNavigation {
    private val _tempRect = Rect()
    private val _minimumFlingVelocity: Float = ViewConfiguration.get(context).scaledMinimumFlingVelocity.toFloat()

    private lateinit var _rootTransformation: BaseTransformation
    private lateinit var _rootBaseView: View

    private var _isMenuLocked: Boolean = false
    private var _isMenuHidden: Boolean = true
    private var _isContentClickableWhenMenuOpened: Boolean = false

    var dragProgress: Float = 0F
        private set
    private var _maxDragDistance: Int = 0
    private var _dragState: Int = 0
    private val _dragHelper = ViewDragHelper.create(this, ViewDragCallback())
    private lateinit var _positionHelper: GravityHelper

    private val _dragListeners = mutableListOf<DragListener>()
    private val _dragStateListeners = mutableListOf<DragStateListener>()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child === _rootBaseView) {
                val rootLeft = _positionHelper.getRootLeft(dragProgress, _maxDragDistance)
                child.layout(rootLeft, top, rootLeft + (right - left), bottom)
            } else {
                child.layout(left, top, right, bottom)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent) = !_isMenuLocked && _dragHelper.shouldInterceptTouchEvent(ev) || shouldBlockClick(ev)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        _dragHelper.processTouchEvent(event)
        return true
    }

    override fun performClick(): Boolean {//todo onClick
        return super.performClick()
    }

    override fun computeScroll() {
        if (_dragHelper.continueSettling(true)) ViewCompat.postInvalidateOnAnimation(this)
    }

    private fun changeMenuVisibility(animated: Boolean, newDragProgress: Float) {
        _isMenuHidden = calculateIsMenuHidden()
        if (animated) {
            val left = _positionHelper.getLeftToSettle(newDragProgress, _maxDragDistance)
            if (_dragHelper.smoothSlideViewTo(_rootBaseView, left, _rootBaseView.top)) {
                ViewCompat.postInvalidateOnAnimation(this)
            }
        } else {
            dragProgress = newDragProgress
            _rootTransformation.transform(dragProgress, _rootBaseView)
            requestLayout()
        }
    }

    private fun shouldBlockClick(event: MotionEvent) = when {
        _isContentClickableWhenMenuOpened -> false
        isMenuOpened() -> {
            _rootBaseView.getHitRect(_tempRect)
            _tempRect.contains(event.x.toInt(), event.y.toInt())
        }
        else -> false
    }

    private fun notifyDrag() = _dragListeners.forEach { listener -> listener.onDrag(dragProgress) }

    private fun notifyDragStart() = _dragStateListeners.forEach { listener -> listener.onDragStart() }

    private fun notifyDragEnd(isOpened: Boolean) = _dragStateListeners.forEach { listener -> listener.onDragEnd(isOpened) }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = Bundle()
        savedState.putParcelable(EXTRA_SUPER, super.onSaveInstanceState())
        savedState.putInt(EXTRA_IS_OPENED, if (dragProgress > 0.5) 1 else 0)
        savedState.putBoolean(EXTRA_SHOULD_BLOCK_CLICK, _isContentClickableWhenMenuOpened)

        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as Bundle

        super.onRestoreInstanceState(savedState.getParcelable(EXTRA_SUPER))

        changeMenuVisibility(false, savedState.getInt(EXTRA_IS_OPENED, 0).toFloat())
        _isMenuHidden = calculateIsMenuHidden()
        _isContentClickableWhenMenuOpened = savedState.getBoolean(EXTRA_SHOULD_BLOCK_CLICK)
    }

    private fun calculateIsMenuHidden() = dragProgress == 0f

    private inner class ViewDragCallback : ViewDragHelper.Callback() {

        private var edgeTouched: Boolean = false

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (_isMenuLocked) return false
            val isOnEdge = edgeTouched
            edgeTouched = false
            return when {
                isMenuClosed() -> child === _rootBaseView && isOnEdge
                child !== _rootBaseView -> {
                    _dragHelper.captureChildView(_rootBaseView, pointerId)
                    false
                }
                else -> true
            }
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            dragProgress = _positionHelper.getDragProgress(left, _maxDragDistance)
            _rootTransformation.transform(dragProgress, _rootBaseView)
            notifyDrag()
            invalidate()
        }

        override fun onViewReleased(releasedChild: View, xVelocity: Float, yvelocity: Float) {
            val left =
                    if (Math.abs(xVelocity) < _minimumFlingVelocity) {
                        _positionHelper.getLeftToSettle(dragProgress, _maxDragDistance)
                    } else {
                        _positionHelper.getLeftAfterFling(xVelocity, _maxDragDistance)
                    }

            _dragHelper.settleCapturedViewAt(left, _rootBaseView.top)
            invalidate()
        }

        override fun onViewDragStateChanged(state: Int) {
            if (_dragState == ViewDragHelper.STATE_IDLE && state != ViewDragHelper.STATE_IDLE) {
                notifyDragStart()
            } else if (_dragState != ViewDragHelper.STATE_IDLE && state == ViewDragHelper.STATE_IDLE) {
                _isMenuHidden = calculateIsMenuHidden()
                notifyDragEnd(isMenuOpened())
            }
            _dragState = state
        }

        override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
            edgeTouched = true
        }

        override fun getViewHorizontalDragRange(child: View): Int = if (child === _rootBaseView) _maxDragDistance else 0

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = _positionHelper.clampViewPosition(left, _maxDragDistance)
    }

    override fun isMenuClosed(): Boolean = _isMenuHidden

    override fun isMenuOpened(): Boolean = !_isMenuHidden

    override fun getLayout(): SlidingMenuLayout = this

    override fun isMenuLocked(): Boolean = _isMenuLocked

    override fun setMenuLocked(locked: Boolean) {
        _isMenuLocked = locked
    }

    override fun closeMenu() {
        closeMenu(true)
    }

    override fun closeMenu(animated: Boolean) {
        changeMenuVisibility(animated, 0f)
    }

    override fun openMenu() {
        openMenu(true)
    }

    override fun openMenu(animated: Boolean) {
        changeMenuVisibility(animated, 1f)
    }

    fun setRootView(view: View) {
        _rootBaseView = view
    }

    fun setContentClickableWhenMenuOpened(contentClickableWhenMenuOpened: Boolean) {
        _isContentClickableWhenMenuOpened = contentClickableWhenMenuOpened
    }

    fun setRootTransformation(transformation: BaseTransformation) {
        _rootTransformation = transformation
    }

    fun setMaxDragDistance(maxDragDistance: Int) {
        this._maxDragDistance = maxDragDistance
    }

    fun setGravity(gravity: SlideGravity) {
        _positionHelper = gravity.createHelper()
        _positionHelper.enableEdgeTrackingOn(_dragHelper)
    }

    fun addDragListener(listener: DragListener) {
        _dragListeners.add(listener)
    }

    fun addDragStateListener(listener: DragStateListener) {
        _dragStateListeners.add(listener)
    }

    fun removeDragListener(listener: DragListener) {
        _dragListeners.remove(listener)
    }

    fun removeDragStateListener(listener: DragStateListener) {
        _dragStateListeners.remove(listener)
    }
}