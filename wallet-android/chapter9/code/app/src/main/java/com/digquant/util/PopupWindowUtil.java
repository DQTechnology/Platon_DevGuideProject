package com.digquant.util;

import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import androidx.constraintlayout.widget.ConstraintLayout;

public class PopupWindowUtil {
    public static PopupWindow Show(View contentView, View anchorView) {

        return Show(contentView, anchorView, 0, 0);
    }

    /**
     * @param contentView 弹出框的内容
     * @param anchorView  停靠对象
     * @param xOffset     停靠X偏移
     * @param yOffset     停靠Y偏移
     * @return
     */
    public static PopupWindow Show(View contentView, View anchorView, int xOffset, int yOffset) {
        // 创建popwindow实例
        final PopupWindow popupWindow = new PopupWindow(
                contentView,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                true);
        // 设置阴影
        popupWindow.setElevation(3);

        popupWindow.setTouchable(true);
        // 计算出指定view的位置
        int[] windowPos = calculatePopWindowPos(contentView, anchorView);

        popupWindow.setElevation(3);

        windowPos[0] -= xOffset;
        windowPos[1] += yOffset;
        //  设置弹出框的位置
        popupWindow.showAtLocation(anchorView, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);

        return popupWindow;
    }

    private static int[] calculatePopWindowPos(final View contentView, final View anchorView) {
        final int[] windowPos = new int[2];
        final int[] anchorLoc = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = ViewUtil.INSTANCE.GetScreenHeight();
        final int screenWidth = ViewUtil.INSTANCE.GetScreenWidth();
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }
}
