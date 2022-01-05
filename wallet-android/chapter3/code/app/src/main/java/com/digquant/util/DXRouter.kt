package com.digquant.util

import android.app.Activity
import android.content.Context
import android.content.Intent

object DXRouter {
    /**
     * @param activity
     * @param activityClass
     */
    fun JumpAndFinish(activity: Activity, activityClass: Class<out Activity?>?) {
        val intent = Intent(activity, activityClass)
        activity.startActivity(intent)
        activity.finish()
    }

    fun JumpTo(activity: Context, intent: Intent?) {
        activity.startActivity(intent)
    }

    fun JumpTo(activity: Context, activityClass: Class<out Activity?>?) {
        val intent = Intent(activity, activityClass)
        activity.startActivity(intent)
    }

    /**
     * @param activity
     * @param activityClass
     */
    fun JumpAndWaitResult(activity: Activity, activityClass: Class<out Activity?>?, reqNO: Int) {
        val intent = Intent(activity, activityClass)
        JumpAndWaitResult(activity, reqNO, intent)
    }

    /**
     * @param activity
     * @param reqNO
     * @param intent
     */
    fun JumpAndWaitResult(activity: Activity, reqNO: Int, intent: Intent?) {
        activity.startActivityForResult(intent, reqNO)
    }
}