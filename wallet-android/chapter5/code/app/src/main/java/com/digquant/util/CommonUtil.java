package com.digquant.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.TextView;


import com.digquant.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    private static final double X_PI = 3.14159265358979324 * 3000.0 / 180.0;

    private static final double EARTH_RADIUS = 6378.137; //地图半径（单位：KM）

    public static void openABrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    public static void makeACall(String phonenUmber, Context context) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phonenUmber));
        context.startActivity(callIntent);
    }

    public static boolean isValidContext(Context c) {

        Activity a = (Activity) c;

        return !a.isFinishing();
    }

    public static boolean isMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static void getMaxMemoryInfo(Context context) {
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        Log.e("MaxMemory:", Long.toString(maxMemory / (1024 * 1024)));
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Log.e("MemoryClass:", Long.toString(activityManager.getMemoryClass()));
        Log.e("LargeMemoryClass:", Long.toString(activityManager.getLargeMemoryClass()));

        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);

    }

    public static void hideSoftKeyBoard(View v) {
        if (v == null) {
            return;
        }
        /* 隐藏软键盘 */
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    public static void showoftKeyBoard(View v) {
        InputMethodManager inputManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(v, 0);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    // 角度转弧度
    private static double rad(double d) {
        return d * Math.PI / 180.0D;
    }

    /**
     * 计算两个经纬度点之间的距离（单位：M）
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS * 1000;
        //s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * TextView中文字通过SpannableString来设置文本（下横线,点击等）属性
     *
     * @param tv     textView控件
     * @param str    原文本
     * @param regExp 正则表达式
     * @returnType void
     */
    public static void richText(TextView tv, String str, String regExp, CharacterStyle... spans) {
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        if (!TextUtils.isEmpty(regExp)) {
            Pattern p = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(str);
            while (m.find()) {
                int start = m.start(0);
                int end = m.end(0);
                //Android4.0以上默认是淡绿色，低版本的是黄色。解决方法就是通过重新设置文字背景为透明色
                tv.setHighlightColor(tv.getResources().getColor(android.R.color.transparent));
                for (CharacterStyle characterStyle : spans) {
                    style.setSpan(characterStyle, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            tv.setText(style);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            tv.setText(str);
        }
    }

    public static SpannableString matcherSearchTitle(int color, String text,
                                                     String keyword) {
        if (text == null) {
            text = "";
        }

        SpannableString s = new SpannableString(text);

        if (!TextUtils.isEmpty(keyword)) {
            Pattern p = Pattern.compile(keyword);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(color), start, end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return s;
    }

    public static int parseColor(String colorStr, String defaultColor) {
        int color;
        try {
            color = Color.parseColor(colorStr);
        } catch (IllegalArgumentException e) {
            color = Color.parseColor(defaultColor);
        }
        return color;
    }

    /**
     * 获取listView滑动的距离
     *
     * @return
     */
    public static int listViewScrollY(AbsListView listView) {
        if (listView == null) {
            return 0;
        }
        View view = listView.getChildAt(0);
        if (view == null) {
            return 0;
        }
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int top = view.getTop();
        return -top + firstVisiblePosition * view.getHeight();
    }


    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 验证身份证
     * @param id
     * @return
     */
    public static boolean IsIDCardNo(String id) {
        if (id == null) {
            return false;
        }
        id = id.toUpperCase();
        if (id.length() != 15 && id.length() != 18) {
            return false;
        }
        int y = 0, m = 0, d = 0;
        if (id.length() == 15) {
            y = Integer.parseInt("19" + id.substring(6, 8), 10);
            m = Integer.parseInt(id.substring(8, 10), 10);
            d = Integer.parseInt(id.substring(10, 12), 10);
        } else {
            if (id.contains("X") && id.indexOf("X") != 17) {
                return false;
            }
            char verifyBit = 0;
            int sum = (id.charAt(0) - '0') * 7 + (id.charAt(1) - '0') * 9 + (id.charAt(2) - '0') * 10
                    + (id.charAt(3) - '0') * 5 + (id.charAt(4) - '0') * 8 + (id.charAt(5) - '0') * 4
                    + (id.charAt(6) - '0') * 2 + (id.charAt(7) - '0') + (id.charAt(8) - '0') * 6
                    + (id.charAt(9) - '0') * 3 + (id.charAt(10) - '0') * 7 + (id.charAt(11) - '0') * 9
                    + (id.charAt(12) - '0') * 10 + (id.charAt(13) - '0') * 5 + (id.charAt(14) - '0') * 8
                    + (id.charAt(15) - '0') * 4 + (id.charAt(16) - '0') * 2;
            sum = sum % 11;
            switch (sum) {
                case 0:
                    verifyBit = '1';
                    break;
                case 1:
                    verifyBit = '0';
                    break;
                case 2:
                    verifyBit = 'X';
                    break;
                case 3:
                    verifyBit = '9';
                    break;
                case 4:
                    verifyBit = '8';
                    break;
                case 5:
                    verifyBit = '7';
                    break;
                case 6:
                    verifyBit = '6';
                    break;
                case 7:
                    verifyBit = '5';
                    break;
                case 8:
                    verifyBit = '4';
                    break;
                case 9:
                    verifyBit = '3';
                    break;
                case 10:
                    verifyBit = '2';
                    break;

            }

            if (id.charAt(17) != verifyBit) {
                return false;
            }
            y = Integer.parseInt(id.substring(6, 10), 10);
            m = Integer.parseInt(id.substring(10, 12), 10);
            d = Integer.parseInt(id.substring(12, 14), 10);
        }

        int currentY = Calendar.getInstance().get(Calendar.YEAR);

        /*
         * if(isGecko){ currentY += 1900; }
         */
        if (y > currentY || y < 1870) {
            return false;
        }
        if (m < 1 || m > 12) {
            return false;
        }
        if (d < 1 || d > 31) {
            return false;
        }
        return true;
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     *
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 将 BD-09 坐标转换成 GCJ-02 坐标
     * GoogleMap和高德map用的是同一个坐标系GCJ-02
     */
    public static double[] bd_decrypt(double bd_lat, double bd_lon) {
        double gg_lat = 0.0;
        double gg_lon = 0.0;
        double location[] = new double[2];
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        gg_lon = z * Math.cos(theta);
        gg_lat = z * Math.sin(theta);
        location[0] = gg_lat;
        location[1] = gg_lon;
        return location;
    }


    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric;
    }

    /**
     * 验证手机格式
     */
    public static boolean IsMobileNO(String mobiles) {
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][3456789]\\d{9}";
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }

    public static void sendMsg(Context context, String phoneNumber, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", message);
        context.startActivity(intent);
    }

    /*public static <T> List<T> deepCopyList(List<T> src) {
        List<T> dest = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            dest = (List<T>) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dest;
    }*/

    public static void CpyTextToClipboard(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("plain text", text));
        ToastUtil.showLongToast(context, context.getResources().getString(R.string.copy_succeed));
    }

    public static String getTextFromClipboard(Context context) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (!cm.hasPrimaryClip()) {
            return "";
        } else {
            return cm.getPrimaryClip().getItemAt(0).coerceToText(context).toString();
        }
    }

    public static boolean validUrl(String uri) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(uri);
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            try {
                conn.connect();
            } catch (Exception e) {
                return false;
            }

            int code = conn.getResponseCode();
            if ((code >= 100) && (code < 400)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static boolean ping(String ip) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("ping -c 3 " + ip);
            return process.waitFor() == 0;
        } catch (Exception e) {
        }
        return false;
    }

    public static String transport(String inputStr) {
        char arr[] = inputStr.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == ' ') {
                arr[i] = '\u3000';
            } else if (arr[i] < '\177') {
                arr[i] = (char) (arr[i] + 65248);
            }

        }
        return new String(arr);
    }
}
