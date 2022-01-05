package com

import android.app.Application
import com.digquant.util.ResourceUtil

class DiggingApplication : Application() {

    init {
        ResourceUtil.SetContext(this)
    }
}