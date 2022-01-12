package com.digquant.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class ResourceUtil {
    private static Context m_context;

    public static void SetContext(Context context) {
        m_context = context;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable GetDrawable(int resId){
        return m_context.getResources().getDrawable(resId);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static int GetColor(int resId){
        return m_context.getResources().getColor(resId);
    }

    public static String GetString(int resId) {
        return m_context.getResources().getString(resId);
    }
}
