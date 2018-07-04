package com.paultech.util;

public class StringUtil {
    private StringUtil() {}
    public static String convertStringToCamelCase(String original, String delimiterRegex) {
        String[] stringArray = original.split(delimiterRegex);
        boolean firstSkipped = false;
        StringBuilder sb = new StringBuilder(20);
        for (String s : stringArray) {
            if (!firstSkipped) {
                sb.append(s);
                firstSkipped = true;
            } else {
                sb.append(capitalizeFirstLetter(s));
            }
        }
        return sb.toString();
    }

    private static String capitalizeFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
