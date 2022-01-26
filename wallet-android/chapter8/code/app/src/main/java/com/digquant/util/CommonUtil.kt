package com.digquant.util

import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Process
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.TextView
import com.digquant.R
import com.digquant.util.ToastUtil.showLongToast
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.regex.Pattern

object CommonUtil {
    private const val X_PI = 3.14159265358979324 * 3000.0 / 180.0
    private const val EARTH_RADIUS = 6378.137 //地图半径（单位：KM）
    fun openABrowser(context: Context, url: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun makeACall(phonenUmber: String, context: Context) {
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phonenUmber"))
        context.startActivity(callIntent)
    }

    fun isValidContext(c: Context): Boolean {
        val a = c as Activity
        return !a.isFinishing
    }

    fun isMainProcess(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos = am.runningAppProcesses
        val mainProcessName = context.packageName
        val myPid = Process.myPid()
        for (info in processInfos) {
            if (info.pid == myPid && mainProcessName == info.processName) {
                return true
            }
        }
        return false
    }

    fun getMaxMemoryInfo(context: Context) {
        val rt = Runtime.getRuntime()
        val maxMemory = rt.maxMemory()
        Log.e("MaxMemory:", java.lang.Long.toString(maxMemory / (1024 * 1024)))
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        Log.e("MemoryClass:", java.lang.Long.toString(activityManager.memoryClass.toLong()))
        Log.e(
            "LargeMemoryClass:",
            java.lang.Long.toString(activityManager.largeMemoryClass.toLong())
        )
        val info = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(info)
    }

    fun hideSoftKeyBoard(v: View?) {
        if (v == null) {
            return
        }
        /* 隐藏软键盘 */
        val imm = v.context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
        }
    }

    fun showoftKeyBoard(v: View) {
        val inputManager =
            v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(v, 0)
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    // 角度转弧度
    private fun rad(d: Double): Double {
        return d * Math.PI / 180.0
    }

    /**
     * 计算两个经纬度点之间的距离（单位：M）
     */
    fun getDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val radLat1 = rad(lat1)
        val radLat2 = rad(lat2)
        val a = radLat1 - radLat2
        val b = rad(lng1) - rad(lng2)
        var s = 2 * Math.asin(
            Math.sqrt(
                Math.pow(Math.sin(a / 2), 2.0) +
                        Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2.0)
            )
        )
        s = s * EARTH_RADIUS * 1000
        //s = Math.round(s * 10000) / 10000;
        return s
    }

    /**
     * TextView中文字通过SpannableString来设置文本（下横线,点击等）属性
     *
     * @param tv     textView控件
     * @param str    原文本
     * @param regExp 正则表达式
     * @returnType void
     */
    fun richText(tv: TextView, str: String?, regExp: String?, vararg spans: CharacterStyle?) {
        val style = SpannableStringBuilder(str)
        if (!TextUtils.isEmpty(regExp)) {
            val p = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE)
            val m = p.matcher(str)
            while (m.find()) {
                val start = m.start(0)
                val end = m.end(0)
                //Android4.0以上默认是淡绿色，低版本的是黄色。解决方法就是通过重新设置文字背景为透明色
                tv.highlightColor = tv.resources.getColor(R.color.transparent)
                for (characterStyle in spans) {
                    style.setSpan(characterStyle, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            tv.text = style
            tv.movementMethod = LinkMovementMethod.getInstance()
        } else {
            tv.text = str
        }
    }

    fun matcherSearchTitle(
        color: Int, text: String?,
        keyword: String?
    ): SpannableString {
        var text = text
        if (text == null) {
            text = ""
        }
        val s = SpannableString(text)
        if (!TextUtils.isEmpty(keyword)) {
            val p = Pattern.compile(keyword)
            val m = p.matcher(s)
            while (m.find()) {
                val start = m.start()
                val end = m.end()
                s.setSpan(
                    ForegroundColorSpan(color), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return s
    }

    fun parseColor(colorStr: String?, defaultColor: String?): Int {
        val color: Int
        color = try {
            Color.parseColor(colorStr)
        } catch (e: IllegalArgumentException) {
            Color.parseColor(defaultColor)
        }
        return color
    }

    /**
     * 获取listView滑动的距离
     *
     * @return
     */
    fun listViewScrollY(listView: AbsListView?): Int {
        if (listView == null) {
            return 0
        }
        val view = listView.getChildAt(0) ?: return 0
        val firstVisiblePosition = listView.firstVisiblePosition
        val top = view.top
        return -top + firstVisiblePosition * view.height
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * 验证身份证
     * @param id
     * @return
     */
    fun IsIDCardNo(id: String?): Boolean {
        var id = id
        if (id == null) {
            return false
        }
        id = id.toUpperCase()
        if (id.length != 15 && id.length != 18) {
            return false
        }
        var y = 0
        var m = 0
        var d = 0
        if (id.length == 15) {
            y = ("19" + id.substring(6, 8)).toInt(10)
            m = id.substring(8, 10).toInt(10)
            d = id.substring(10, 12).toInt(10)
        } else {
            if (id.contains("X") && id.indexOf("X") != 17) {
                return false
            }
            var verifyBit = 0.toChar()
            var sum =
                (id[0] - '0') * 7 + (id[1] - '0') * 9 + (id[2] - '0') * 10 + (id[3] - '0') * 5 + (id[4] - '0') * 8 + (id[5] - '0') * 4 + (id[6] - '0') * 2 + (id[7] - '0') + (id[8] - '0') * 6 + (id[9] - '0') * 3 + (id[10] - '0') * 7 + (id[11] - '0') * 9 + (id[12] - '0') * 10 + (id[13] - '0') * 5 + (id[14] - '0') * 8 + (id[15] - '0') * 4 + (id[16] - '0') * 2
            sum = sum % 11
            when (sum) {
                0 -> verifyBit = '1'
                1 -> verifyBit = '0'
                2 -> verifyBit = 'X'
                3 -> verifyBit = '9'
                4 -> verifyBit = '8'
                5 -> verifyBit = '7'
                6 -> verifyBit = '6'
                7 -> verifyBit = '5'
                8 -> verifyBit = '4'
                9 -> verifyBit = '3'
                10 -> verifyBit = '2'
            }
            if (id[17] != verifyBit) {
                return false
            }
            y = id.substring(6, 10).toInt(10)
            m = id.substring(10, 12).toInt(10)
            d = id.substring(12, 14).toInt(10)
        }
        val currentY = Calendar.getInstance()[Calendar.YEAR]

        /*
         * if(isGecko){ currentY += 1900; }
         */if (y > currentY || y < 1870) {
            return false
        }
        if (m < 1 || m > 12) {
            return false
        }
        return if (d < 1 || d > 31) {
            false
        } else true
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     *
     * @param str
     * @return
     */
    fun stringFilter(str: String): String {
        var str = str
        str = str.replace("【".toRegex(), "[").replace("】".toRegex(), "]")
            .replace("！".toRegex(), "!").replace("：".toRegex(), ":") // 替换中文标号
        val regEx = "[『』]" // 清除掉特殊字符
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return m.replaceAll("").trim { it <= ' ' }
    }

    /**
     * 将 BD-09 坐标转换成 GCJ-02 坐标
     * GoogleMap和高德map用的是同一个坐标系GCJ-02
     */
    fun bd_decrypt(bd_lat: Double, bd_lon: Double): DoubleArray {
        var gg_lat = 0.0
        var gg_lon = 0.0
        val location = DoubleArray(2)
        val x = bd_lon - 0.0065
        val y = bd_lat - 0.006
        val z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI)
        val theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI)
        gg_lon = z * Math.cos(theta)
        gg_lat = z * Math.sin(theta)
        location[0] = gg_lat
        location[1] = gg_lon
        return location
    }

    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val metric = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(metric)
        return metric
    }

    /**
     * 验证手机格式
     */
    fun IsMobileNO(mobiles: String): Boolean {
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        val telRegex = Regex("[1][3456789]\\d{9}")
        return if (TextUtils.isEmpty(mobiles)) {
            false
        } else {
            mobiles.matches(telRegex)
        }
    }

    fun sendMsg(context: Context, phoneNumber: String, message: String?) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber"))
        intent.putExtra("sms_body", message)
        context.startActivity(intent)
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

    fun CpyTextToClipboard(context: Context, text: String?) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("plain text", text))
        showLongToast(context, context.resources.getString(R.string.copy_succeed))
    }

    fun getTextFromClipboard(context: Context): String {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return if (!cm.hasPrimaryClip()) {
            ""
        } else {
            cm.primaryClip!!.getItemAt(0).coerceToText(context).toString()
        }
    }

    fun validUrl(uri: String?): Boolean {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(uri)
            conn = url.openConnection() as HttpURLConnection
            conn.useCaches = false
            conn!!.instanceFollowRedirects = true
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            try {
                conn.connect()
            } catch (e: Exception) {
                return false
            }
            val code = conn.responseCode
            if (code >= 100 && code < 400) {
                true
            } else false
        } catch (e: Exception) {
            false
        } finally {
            conn?.disconnect()
        }
    }

    fun ping(ip: String): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val process = runtime.exec("ping -c 3 $ip")
            return process.waitFor() == 0
        } catch (e: Exception) {
        }
        return false
    }

    fun transport(inputStr: String): String {
        val arr = inputStr.toCharArray()
        for (i in arr.indices) {
            if (arr[i] == ' ') {
                arr[i] = '\u3000'
            } else if (arr[i] < '\u007f') {
                arr[i] = (arr[i] + 65248)
            }
        }
        return String(arr)
    }
}
