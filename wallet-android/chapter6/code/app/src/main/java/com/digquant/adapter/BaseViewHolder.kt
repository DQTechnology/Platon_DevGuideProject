package com.digquant.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

     open fun OnRender(position: Int) {

    }
}
