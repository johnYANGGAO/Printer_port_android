package com.bjut.printer.utils;

import java.util.regex.Pattern;

/**
 * Created by johnsmac on 9/16/16.
 */
public class ProcessCharacterUtil {

    public static boolean isNumber(String str) {
        if (str == null || str.trim().equals("")) {
            return false;

        }
        char[] charArray = str.toCharArray();
        int dotnum = 0;
        for (int i = str.length(); --i >= 0; ) {
            char currentchar = charArray[i];
            boolean isdot = false;
            if (currentchar == '.') {
                isdot = true;
                dotnum++;
            }
            if (!Character.isDigit(str.charAt(i)) && isdot == false) {
                return false;
            }
        }
        if (dotnum > 1) return false;
        return isNumberExt(str);

    }

    public static boolean isNumberExt(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.*[0-9]*");
        return pattern.matcher(str).matches();
    }

}
