package com.paultech.util;

import java.util.Arrays;
import java.util.Optional;

public class StringUtil {
    private StringUtil() {}
    public static String convertStringToCamelCase(String original, String delimiterRegex) {

        Optional<String> reducedString = Arrays
                .stream(original.split(delimiterRegex))
                .map(StringUtil::capitalizeFirstLetter)
                .reduce((s, s2) -> s + s2);
        if (reducedString.isPresent()) {
            return decapitalizeFirstLetter(reducedString.get());
        } else {
            return null;
        }
    }

    private static String capitalizeFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private static String decapitalizeFirstLetter(String word) {
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }
}
