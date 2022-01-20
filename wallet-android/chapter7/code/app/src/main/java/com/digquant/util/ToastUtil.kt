package com.digquant.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.digquant.R
import com.digquant.util.DensityUtil.DP2PX

/**
 * @author matrixelement
 * @date 2016/7/9
 */
object ToastUtil {
    @Volatile
    private var mToast: Toast? = null

    /**
     * 双重锁定，使用同一个Toast实例,就不会连续弹出多个toast
     * 多次点击每用新的覆盖之前弹出的toast
     *
     * @param context
     * @return
     */
    private fun getInstance(context: Context): Toast? {
        if (mToast == null) {
            synchronized(ToastUtil::class.java) {
                if (mToast == null) {
                    mToast = Toast(context)
                }
            }
        }
        return mToast
    }

    private fun toast(context: Context, msg: String, duration: Int) {
        val toast = getInstance(context)
        toast!!.view = buildToastTextView(context, msg)
        toast.duration = duration
        toast.setGravity(Gravity.TOP, 0, DP2PX(context, 10f))
        toast.show()
    }

    private fun buildToastTextView(context: Context, msg: String): View {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_transient_notification, null)
        val tv = view.findViewById<TextView>(R.id.message)
        tv.text = msg
        return view
    }

    fun showShortToast(context: Context, msgResId: Int) {
        toast(context, context.resources.getString(msgResId), Toast.LENGTH_SHORT)
    }

    fun showShortToast(context: Context, message: String) {
        toast(context, message, Toast.LENGTH_SHORT)
    }

    fun showLongToast(context: Context, msgResId: Int) {
        toast(context, context.resources.getString(msgResId), Toast.LENGTH_LONG)
    }

    fun showLongToast(context: Context, message: String) {
        toast(context, message, Toast.LENGTH_LONG)
    }
}
