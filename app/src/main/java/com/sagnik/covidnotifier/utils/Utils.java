package com.sagnik.covidnotifier.utils;

import java.text.DecimalFormat;

public class Utils {
    public static String formatNumber(long value) {
        return formatNumber(value, false);
    }

    public static String formatNumber(long value, boolean explicitPlus){
        DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0");
        return (explicitPlus && value >= 0 ? "+" : "") + df.format(value);
    }
}
