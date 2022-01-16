package com.digquant.util;

import android.content.Context;


import com.digquant.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;

public class StringUtil {
    public static String Join(String... args) {
        if (args == null) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String str : args) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    public static String FormatNumber(double val) {


        return FormatNumber(val, "##.00");
    }
    public static String FormatNumber(double val, String format) {
        DecimalFormat df = new DecimalFormat(format);//输出"0.12"


        return df.format(val);
    }
    public static String Join2(String split, String... args) {
        if (args == null) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String str : args) {
            stringBuilder.append(str);
            stringBuilder.append(split);
        }
        stringBuilder.setLength(stringBuilder.length() - split.length());
        return stringBuilder.toString();
    }


    public static String SubString(String srcStr, int subLen) {
        if (subLen > srcStr.length()) {
            return srcStr;
        }
        return srcStr.substring(0, subLen - 1);
    }

    public static boolean IsPattern(String path) {
        if (path == null) {
            return false;
        }
        boolean uriVar = false;
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '*' || c == '?') {
                return true;
            }
            if (c == '{') {
                uriVar = true;
                continue;
            }
            if (c == '}' && uriVar) {
                return true;
            }
        }
        return false;
    }

    public static boolean IsEqual(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 != null) {
            return str1.equals(str2);
        }
        return false;
    }

    public static boolean IsEqualIgnoreCase(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 != null) {
            return str1.equalsIgnoreCase(str2);
        }
        return false;
    }


    public static String FormatNumber(String format, int number) {
        int formatLen = format.length();
        StringBuilder stringBuilder = new StringBuilder(formatLen);
        stringBuilder.setLength(formatLen);
        int endIndex = formatLen - 1;
        for (int i = 0; i < formatLen; ++i) {
            int digit = number - (number / 10) * 10;
            stringBuilder.setCharAt(endIndex - i, (char) (digit + '0'));
            number /= 10;
            if (number == 0) {
                for (++i; i < formatLen; ++i) {
                    stringBuilder.setCharAt(endIndex - i, '0');
                }
                break;
            }
        }
        return stringBuilder.toString();
    }


    public static String JoinCollection(Collection<String> strList, String split) {
        if (strList == null || strList.size() == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : strList) {

            stringBuilder.append(str);
            stringBuilder.append(split);
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static boolean IsEmpty(String txt) {
        if (txt == null || txt.length() == 0) {
            return true;
        }
        return false;
    }

    private final static int[] SIZE_TABLE = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999,
            Integer.MAX_VALUE};

    /**
     * calculate the size of an integer number
     *
     * @param x
     * @return
     */
    public static int sizeOfInt(int x) {
        for (int i = 0; ; i++)
            if (x <= SIZE_TABLE[i]) {
                return i + 1;
            }
    }

    /**
     * Judge whether each character of the string equals
     *
     * @param str
     * @return
     */
    public static boolean isCharEqual(String str) {
        return str.replace(str.charAt(0), ' ').trim().length() == 0;
    }

    /**
     * Determines if the string is a digit
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Judge whether the string is whitespace, empty ("") or null.
     *
     * @param str
     * @return
     */
    public static boolean equalsNull(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0 || str.equalsIgnoreCase("null")) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串数字显示按千分位显示
     */
    public static String formatBalance(double price, boolean halfUp) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(8);//设置最大的小数位数
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setGroupingSize(3);//设置分组大小，也就是显示逗号的位置
        decimalFormat.setRoundingMode(halfUp ? RoundingMode.HALF_UP : RoundingMode.FLOOR);
        return decimalFormat.format(new BigDecimal(NumberParserUtils.getPrettyNumber(price, 8)));
    }

    /**
     * 字符串数字显示按千分位显示
     */
    public static String formatBalance(String price, boolean halfUp) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(8);//设置最大的小数位数
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setGroupingSize(3);//设置分组大小，也就是显示逗号的位置
        if (halfUp) {
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        }
        return decimalFormat.format(new BigDecimal(price));
    }

    /**
     * 字符串数字显示按千分位显示
     */
    public static String formatBalance(String price) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(8);//设置最大的小数位数
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setGroupingSize(3);//设置分组大小，也就是显示逗号的位置
        return decimalFormat.format(new BigDecimal(NumberParserUtils.getPrettyNumber(price, 8)));
    }

    public static String formatBalanceWithoutFractionDigits(String price) {
        return formatBalance(price, 0, 0);
    }

    /**
     * 字符串数字显示按千分位显示
     */
    public static String formatBalance(String price, int maxFractionDigits) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(maxFractionDigits);//设置最大的小数位数
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setGroupingSize(3);//设置分组大小，也就是显示逗号的位置
        return decimalFormat.format(new BigDecimal(NumberParserUtils.getPrettyNumber(price, maxFractionDigits)));
    }

    /**
     * 字符串数字显示按千分位显示
     */
    public static String formatBalance(String price, int minimumFractionDigits, int maxFractionDigits) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(maxFractionDigits);//设置最大的小数位数
        decimalFormat.setMinimumFractionDigits(minimumFractionDigits);
        decimalFormat.setGroupingSize(3);//设置分组大小，也就是显示逗号的位置
        return decimalFormat.format(new BigDecimal(NumberParserUtils.getPrettyNumber(price, maxFractionDigits)));
    }

    /**
     * 字符串数字显示按千分位显示
     */
    public static String formatBalanceWithoutMinFraction(String price) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(8);//设置最大的小数位数
        decimalFormat.setMinimumFractionDigits(0);
        decimalFormat.setGroupingSize(3);//设置分组大小，也就是显示逗号的位置
        return decimalFormat.format(new BigDecimal(NumberParserUtils.getPrettyNumber(price, 8)));
    }


    /**
     * 获取转账金额的数量级描述
     *
     * @param transferAmount
     * @return
     */
    public static String getAmountMagnitudes(Context context, String transferAmount) {
        double amount = NumberParserUtils.parseDouble(transferAmount);
        //万亿
        if (amount >= Constants.Magnitudes.TRILLION) {
            return context.getString(R.string.msg_trillion);
        } else if (amount >= Constants.Magnitudes.HUNDRED_BILLION) {
            return context.getString(R.string.msg_hundred_billion);
        } else if (amount >= Constants.Magnitudes.TEN_BILLION) {
            return context.getString(R.string.msg_ten_billion);
        } else if (amount >= Constants.Magnitudes.BILLION) {
            return context.getString(R.string.msg_billion);
        } else if (amount >= Constants.Magnitudes.HUNDRED_MILLION) {
            return context.getString(R.string.msg_hundred_million);
        } else if (amount >= Constants.Magnitudes.TEN_MILLION) {
            return context.getString(R.string.msg_ten_million);
        } else if (amount >= Constants.Magnitudes.MILLION) {
            return context.getString(R.string.msg_million);
        } else if (amount >= Constants.Magnitudes.HUNDRED_THOUSAND) {
            return context.getString(R.string.msg_hundred_thousand);
        } else if (amount >= Constants.Magnitudes.TEN_THOUSAND) {
            return context.getString(R.string.msg_ten_thousand);
        } else if (amount >= Constants.Magnitudes.THOUSAND) {
            return context.getString(R.string.msg_thousand);
        } else if (amount >= Constants.Magnitudes.HUNDRED) {
            return context.getString(R.string.msg_hundred);
        } else {
            return "";
        }
    }

}
