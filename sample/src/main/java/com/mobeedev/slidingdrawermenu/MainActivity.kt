package com.mobeedev.slidingdrawermenu

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.mobeedev.library.SlidingMenuBuilder
import com.mobeedev.library.SlidingNavigation
import com.mobeedev.slidingdrawermenu.menu.SlideMenuAdapter
import com.mobeedev.slidingdrawermenu.menu.SlideMenuItem

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var slidingNavigation: SlidingNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setUpMenu(savedInstanceState)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Made By Szymon Kraus. Portfolio available at mobeedev.com", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }


    private fun setUpMenu(savedInstanceState: Bundle?) {
        slidingNavigation = SlidingMenuBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.fragment_menu)
                .withToolbarMenuToggle(toolbar)
                .inject()

        val tmpElements = mutableListOf(SlideMenuItem(R.drawable.ibeether_for_mobee_avatar_2), SlideMenuItem(R.drawable.hame_mate_avatar), SlideMenuItem(R.drawable.baloon_charger_avatar))
        val adapter = SlideMenuAdapter(tmpElements)

        val menu = findViewById<RecyclerView>(R.id.menu_recycler)
        menu.layoutManager = LinearLayoutManager(this)
        menu.isNestedScrollingEnabled = false
        menu.adapter = adapter
    }
}
