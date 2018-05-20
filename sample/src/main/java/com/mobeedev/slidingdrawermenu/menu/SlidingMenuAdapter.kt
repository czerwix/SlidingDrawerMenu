package com.mobeedev.slidingdrawermenu.menu

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobeedev.slidingdrawermenu.R
import kotlinx.android.synthetic.main.menu_element.view.*

class SlideMenuAdapter(val menuElements: MutableList<SlideMenuItem>) : RecyclerView.Adapter<SlidingMenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SlidingMenuViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.menu_element, parent, false))

    override fun getItemCount() = menuElements.size

    override fun onBindViewHolder(holder: SlidingMenuViewHolder, position: Int) = holder.bind(menuElements[position])

}

class SlidingMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: SlideMenuItem) = with(itemView) {
        element_picture.setImageResource(item.image)
    }
}