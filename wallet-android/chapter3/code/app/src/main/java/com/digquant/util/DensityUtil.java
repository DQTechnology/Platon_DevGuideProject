package com.digquant.util;

import android.content.Context;
import android.util.TypedValue;

public class DensityUtil {



    public static int DP2PX(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dp * scale + 0.5f);
    }

    public static int PX2DP(Context context, float px) {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (px / scale + 0.5f);
    }

    public static int SP2PX(Context context, float sp) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
