package com.digquant.util;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ViewUtil {
    private static Window _window;

    private static int m_statusHeight = 0;

    public static void SetWindow(Window window) {
        _window = window;

        initStatusBarHeight();
    }


    private static void initStatusBarHeight() {
        Context context = _window.getContext();
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {

            m_statusHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
    }


    public static int GetScreenHeight() {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();

        _window.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);

        return localDisplayMetrics.heightPixels;
    }


    public static int GetScreenWidth() {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();

        _window.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);

        return localDisplayMetrics.widthPixels;
    }


    public static int GetStatusBarHeight() {
        return m_statusHeight;
    }

    public static void SetFontBold(TextView tv) {
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
    }

    public static void SetBottomLine(TextView tv) {
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);

        tp.setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tp.setAntiAlias(true);//抗锯齿
    }


    public static void SetStatusBarColorToBlack(Window window) {
        View decor = window.getDecorView();

        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


    }

    public static View GeneratePage(LayoutInflater inflater, int pageId) {
        return inflater.inflate(pageId, null, false);
    }


    public static void SetStatusBarColorToLight(Window window) {
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    public static View BuildPaddingVerticalView(Context context) {
        View view = new View(context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);

        return view;
    }

    public static View BuildPaddingHorizontalView(Context context) {
        View view = new View(context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);

        return view;
    }


}
