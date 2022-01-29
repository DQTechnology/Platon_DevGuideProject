package com.digquant.util

import android.text.TextUtils
import java.lang.Exception
import java.lang.RuntimeException
import java.math.BigDecimal

object AmountUtil {
    private const val VALUE_1E18 = "1e18"
    private const val VALUE_1E2 = "1e2"

    /**
     * 处理余额，最多保留八位小数，多余部分截断显示
     *
     * @param value
     * @param maxDigit
     * @return
     */
    fun getPrettyBalance(value: String?, maxDigit: Int): String {
        if (TextUtils.isEmpty(value)) {
            return BigDecimal.ZERO.toPlainString()
        }
        if (maxDigit < 0) {
            throw RuntimeException("unsupported scale")
        }
        try {
            //value除以10的10次方然后取整就是保留八位小数截断显示的值。因为value是真实值乘以10的18次方后的值
            var bigDecimal = BigDecimal(value).divide(BigDecimal(10).pow(18 - maxDigit))
            bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN)
            return bigDecimal.multiply(BigDecimal(10).pow(18 - maxDigit)).stripTrailingZeros()
                .toPlainString()
        } catch (exp: Exception) {

        }
        return BigDecimal.ZERO.toPlainString()
    }

    /**
     * 处理手续费，最多保留八位小数，多余部分截断，并根据多余部分是否有值，有值则加1
     * 例如0.123456789 --> 0.12345679
     * 例如0.999999999 --> 1.00
     *
     * @param value
     * @param maxDigit
     * @return
     */
    fun getPrettyFee(value: String?, maxDigit: Int): String {
        if (TextUtils.isEmpty(value)) {
            return BigDecimal.ZERO.toPlainString()
        }
        if (maxDigit < 0) {
            throw RuntimeException("unsupported scale")
        }
        try {
            //value除以10的10次方然后取整就是保留八位小数截断显示的值。因为value是真实值乘以10的18次方后的值
            val bigDecimal = BigDecimal(value).divide(BigDecimal(10).pow(18 - maxDigit))
            //是否有小数位
            var resultBigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_DOWN)
            //有小数位
            if (bigDecimal.compareTo(resultBigDecimal) > 0) {
                resultBigDecimal = resultBigDecimal.add(BigDecimal.ONE)
            }
            return resultBigDecimal.multiply(BigDecimal(10).pow(18 - maxDigit)).stripTrailingZeros()
                .toPlainString()
        } catch (e: Exception) {

        }
        return BigDecimal.ZERO.toPlainString()
    }

    /**
     * von to lat  value除以10的18次方，并且保留小数点后八位
     *
     * @param value
     * @return
     */
    fun convertVonToLat(value: String?, max: Int): String {
        return NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(value, VALUE_1E18), max)
    }

    fun convertVonToLatWithFractionDigits(value: String?, fractionDigits: Int): String {
        return NumberParserUtils.parseStringWithFractionDigits(
            BigDecimalUtil.div(
                value,
                VALUE_1E18,
                fractionDigits
            ), fractionDigits
        )
    }

    /**
     * von to lat  value除以10的18次方，默认保留小数点后八位
     *
     * @param value
     * @return
     */
    fun convertVonToLat(value: String?): String {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(value, VALUE_1E18))
    }

    /**
     * 格式化金额文本
     *
     * @param amount
     * @return
     */
    fun formatAmountText(amount: String?): String {


        return if (TextUtils.isEmpty(amount)) "--" else StringUtil.formatBalance(
            BigDecimalUtil.div(amount, VALUE_1E18)
        )
    }


    fun formatAmountText3(amount: String?): String {


        return if (TextUtils.isEmpty(amount)) "--" else StringUtil.formatBalance(amount)
    }
    /**
     * 格式化金额文本
     *
     * @param amount
     * @return
     */
    fun formatAmountText(amount: String?, maxFractionDigits: Int): String {
        return if (TextUtils.isEmpty(amount) || "0" == amount) "--" else StringUtil.formatBalance(
            BigDecimalUtil.div(amount, VALUE_1E18, maxFractionDigits),
            maxFractionDigits
        )
    }

    /**
     * 格式化金额文本
     *
     * @param amount
     * @return
     */
    fun formatAmountText2(amount: String, maxFractionDigits: Int): String {
        return if (TextUtils.isEmpty(amount) || "0" == amount) "0.00" else StringUtil.formatBalance(
            BigDecimalUtil.div(amount, VALUE_1E18, maxFractionDigits),
            maxFractionDigits
        )
    }
}
