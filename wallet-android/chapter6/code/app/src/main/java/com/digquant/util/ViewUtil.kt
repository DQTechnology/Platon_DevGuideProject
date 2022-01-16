package com.digquant.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView

object ViewUtil {
    private var _window: Window? = null
    private var m_statusHeight = 0
    fun SetWindow(window: Window?) {
        _window = window
        initStatusBarHeight()
    }

    private fun initStatusBarHeight() {
        val context = _window!!.context
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            m_statusHeight = context.resources.getDimensionPixelSize(resourceId)
        }
    }

    fun GetScreenHeight(): Int {
        val localDisplayMetrics = DisplayMetrics()
        _window!!.windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
        return localDisplayMetrics.heightPixels
    }

    fun GetScreenWidth(): Int {
        val localDisplayMetrics = DisplayMetrics()
        _window!!.windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
        return localDisplayMetrics.widthPixels
    }

    fun GetStatusBarHeight(): Int {
        return m_statusHeight
    }

    fun SetStatusBarMargin(view: View) {
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = ViewUtil.GetStatusBarHeight()
        view.layoutParams = layoutParams
    }

    fun SetFontBold(tv: TextView) {
        val tp = tv.paint
        tp.isFakeBoldText = true
    }

    fun SetBottomLine(tv: TextView) {
        val tp = tv.paint
        tp.isFakeBoldText = true
        tp.flags = Paint.UNDERLINE_TEXT_FLAG //下划线
        tp.isAntiAlias = true //抗锯齿
    }

    fun SetStatusBarColorToBlack(window: Window) {
        val decor = window.decorView
        decor.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun GeneratePage(inflater: LayoutInflater, pageId: Int): View {
        return inflater.inflate(pageId, null, false)
    }

    fun SetStatusBarColorToLight(window: Window) {
        val decor = window.decorView
        decor.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    fun BuildPaddingVerticalView(context: Context?): View {
        val view = View(context)
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
        layoutParams.weight = 1f
        view.layoutParams = layoutParams
        return view
    }

    fun BuildPaddingHorizontalView(context: Context?): View {
        val view = View(context)
        val layoutParams = LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.weight = 1f
        view.layoutParams = layoutParams
        return view
    }


}