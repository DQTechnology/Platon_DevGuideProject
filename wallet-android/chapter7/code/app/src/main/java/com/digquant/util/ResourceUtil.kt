package com.digquant.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable

object ResourceUtil {
    private var m_context: Context? = null
    fun SetContext(context: Context?) {
        m_context = context
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun GetDrawable(resId: Int): Drawable {
        return m_context!!.resources.getDrawable(resId)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun GetColor(resId: Int): Int {
        return m_context!!.resources.getColor(resId)
    }

    fun GetString(resId: Int): String {
        return m_context!!.resources.getString(resId)
    }
}
