package com.sagnik.covidnotifier.utils;

import java.text.DecimalFormat;

public class Utils {
    public static final String NUMBER_FORMAT = "##,##,##,##,##,##,##0";
    public static final String EXPLICIT_PLUS = "+";
    public static final String EMPTY_STRING = "";

    public static String formatNumber(long value) {
        return formatNumber(value, false);
    }

    public static String formatNumber(long value, boolean explicitPlus){
        DecimalFormat df = new DecimalFormat(NUMBER_FORMAT);
        return (explicitPlus && value >= 0 ? EXPLICIT_PLUS : EMPTY_STRING) + df.format(value);
    }
}
