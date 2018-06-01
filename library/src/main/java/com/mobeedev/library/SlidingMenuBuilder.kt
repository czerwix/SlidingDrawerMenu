package com.mobeedev.library

import android.app.Activity
import android.os.Bundle
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import android.support.annotation.LayoutRes
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.mobeedev.library.dragstate.DragListener
import com.mobeedev.library.dragstate.DragStateListener
import com.mobeedev.library.gravity.SlideGravity
import com.mobeedev.library.transform.*
import com.mobeedev.library.util.ActionBarToggleAdapter
import com.mobeedev.library.util.DrawerListenerAdapter
import com.mobeedev.library.util.HiddenMenuClickConsumer
import java.util.*

private const val DEFAULT_END_SCALE = 0.65f
private const val DEFAULT_END_ELEVATION_DP = 8
private const val DEFAULT_DRAG_DISTANCE_DP = 180

open class SlidingMenuBuilder(private val activity: Activity) {

    private lateinit var _contentView: ViewGroup
    private lateinit var _menuView: View
    private var _menuLayoutRes: Int = 0

    private val _dragListeners: MutableList<DragListener>
    private val _dragStateListeners: MutableList<DragStateListener>

    private val _transformations: MutableList<BaseTransformation>
    private var _dragDistance: Int = 0

    private var _toolbar: Toolbar? = null
    private var _gravity: SlideGravity

    private var _isMenuOpened: Boolean = false
    private var _isMenuLocked: Boolean = false
    private var _isContentClickableWhenMenuOpened: Boolean = false

    private var _savedState: Bundle? = null

    init {
        this._transformations = ArrayList()
        this._dragListeners = ArrayList()
        this._dragStateListeners = ArrayList()
        this._gravity = SlideGravity.LEFT
        this._dragDistance = dpToPx(DEFAULT_DRAG_DISTANCE_DP)
        this._isContentClickableWhenMenuOpened = true
    }

    fun withMenuView(menuView: View): SlidingMenuBuilder = also { this._menuView = menuView }

    fun withMenuLayout(@LayoutRes menuLayout: Int): SlidingMenuBuilder = also { _menuLayoutRes = menuLayout }

    fun withToolbarMenuToggle(toolbar: Toolbar): SlidingMenuBuilder = also { this._toolbar = toolbar }

    fun withGravity(slideGravity: SlideGravity): SlidingMenuBuilder = also { _gravity = slideGravity }

    fun withContentView(contentView: ViewGroup): SlidingMenuBuilder = also { this._contentView = contentView }

    fun withMenuLocked(locked: Boolean): SlidingMenuBuilder = also { _isMenuLocked = locked }

    fun withSavedState(state: Bundle?): SlidingMenuBuilder = also { _savedState = state }

    fun withMenuOpened(opened: Boolean): SlidingMenuBuilder = also { _isMenuOpened = opened }

    fun withContentClickableWhenMenuOpened(clickable: Boolean): SlidingMenuBuilder = also { _isContentClickableWhenMenuOpened = clickable }

    fun withDragDistance(@IntRange(from = 0) dp: Int): SlidingMenuBuilder = withDragDistancePx(dpToPx(dp))

    fun withDragDistancePx(@IntRange(from = 0) px: Int): SlidingMenuBuilder = also { _dragDistance = px }

    fun withRootViewScale(@FloatRange(from = 0.01) scale: Float): SlidingMenuBuilder = also { _transformations.add(ScaleTransformation(scale)) }

    fun withRootViewElevation(@IntRange(from = 0) elevation: Int): SlidingMenuBuilder = also { return withRootViewElevationPx(dpToPx(elevation)) }

    fun withRootViewElevationPx(@IntRange(from = 0) elevation: Int): SlidingMenuBuilder = also { _transformations.add(ElevationTransformation(elevation.toFloat())) }

    fun withRootViewYTranslation(translation: Int): SlidingMenuBuilder = withRootViewYTranslationPx(dpToPx(translation))

    fun withRootViewYTranslationPx(translation: Int): SlidingMenuBuilder = also { _transformations.add(YTranslationTransformation(translation.toFloat())) }

    fun addRootTransformation(transformation: BaseTransformation): SlidingMenuBuilder = also { _transformations.add(transformation) }

    fun addDragListener(dragListener: DragListener): SlidingMenuBuilder = also { _dragListeners.add(dragListener) }

    fun addDragStateListener(dragStateListener: DragStateListener): SlidingMenuBuilder = also { _dragStateListeners.add(dragStateListener) }

    fun inject(): SlidingNavigation {
        val contentView = getContentView()
        val oldRoot = contentView.getChildAt(0)
        contentView.removeAllViews()

        with(createAndInitNewRoot(oldRoot)) {
            val menu = getMenuViewFor(this)

            initToolbarMenuVisibilityToggle(this, menu)

            val clickConsumer = HiddenMenuClickConsumer(this, activity)

            addView(menu)
            addView(clickConsumer)
            addView(oldRoot)

            contentView.addView(this)

            if (_savedState == null && _isMenuOpened) this.openMenu(false)

            if (!_isContentClickableWhenMenuOpened) setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) closeMenu()

                true
            }

            this.setMenuLocked(_isMenuLocked)

            return this
        }
    }

    private fun createAndInitNewRoot(oldRoot: View): SlidingMenuLayout = with(SlidingMenuLayout(activity)) {
        id = R.id.sliding_menu_root_layout
        setRootTransformation(createCompositeTransformation())
        setMaxDragDistance(_dragDistance)
        setGravity(_gravity)
        rootView = oldRoot
        setContentClickableWhenMenuOpened(_isContentClickableWhenMenuOpened)

        _dragListeners.forEach { addDragListener(it) }
        _dragStateListeners.forEach { addDragStateListener(it) }

        return this
    }

    private fun getContentView(): ViewGroup {
        if (!::_contentView.isInitialized) _contentView = activity.findViewById<View>(android.R.id.content) as ViewGroup

        return _contentView.also {
            if (_contentView.childCount != 1) throw IllegalStateException(activity.getString(R.string.sliding_menu_ex_bad_content_view))
        }
    }

    private fun getMenuViewFor(parent: SlidingMenuLayout): View =
            if (!::_menuView.isInitialized) {
                if (_menuLayoutRes == 0) throw IllegalStateException(activity.getString(R.string.sliding_menu_ex_no_menu_view))

                LayoutInflater.from(activity).inflate(_menuLayoutRes, parent, false)
            } else {
                _menuView
            }

    private fun createCompositeTransformation() =
            if (_transformations.isEmpty()) {
                CompositeTransformation(arrayListOf(
                        ScaleTransformation(DEFAULT_END_SCALE),
                        ElevationTransformation(dpToPx(DEFAULT_END_ELEVATION_DP).toFloat())))
            } else {
                CompositeTransformation(_transformations)
            }

    private fun initToolbarMenuVisibilityToggle(sideNavigation: SlidingMenuLayout, drawer: View) {
        val actionBarAdapter = ActionBarToggleAdapter(sideNavigation, activity)
        val toggle = ActionBarDrawerToggle(activity, actionBarAdapter, _toolbar,
                R.string.sliding_menu_drawer_open,
                R.string.sliding_menu_drawer_close)
        toggle.syncState()

        val listenerAdapter = DrawerListenerAdapter(toggle, drawer)
        sideNavigation.addDragListener(listenerAdapter)
        sideNavigation.addDragStateListener(listenerAdapter)
    }

    private fun dpToPx(dp: Int) = Math.round(activity.resources.displayMetrics.density * dp)
}