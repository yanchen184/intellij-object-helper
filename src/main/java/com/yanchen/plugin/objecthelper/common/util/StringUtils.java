package com.yanchen.plugin.objecthelper.common.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 字符串處理工具類
 */
public class StringUtils {

    /**
     * 判斷字符串是否為空
     *
     * @param str 字符串
     * @return 是否為空
     */
    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判斷字符串是否不為空
     *
     * @param str 字符串
     * @return 是否不為空
     */
    public static boolean isNotEmpty(@Nullable String str) {
        return !isEmpty(str);
    }

    /**
     * 將字符串首字母轉為大寫
     *
     * @param str 字符串
     * @return 首字母大寫的字符串
     */
    @NotNull
    public static String capitalize(@Nullable String str) {
        if (isEmpty(str)) {
            return "";
        }
        char firstChar = str.charAt(0);
        if (Character.isUpperCase(firstChar)) {
            return str;
        }
        return Character.toUpperCase(firstChar) + str.substring(1);
    }

    /**
     * 將字符串首字母轉為小寫
     *
     * @param str 字符串
     * @return 首字母小寫的字符串
     */
    @NotNull
    public static String uncapitalize(@Nullable String str) {
        if (isEmpty(str)) {
            return "";
        }
        char firstChar = str.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return str;
        }
        return Character.toLowerCase(firstChar) + str.substring(1);
    }

    /**
     * 將駝峰式命名轉換為下劃線分隔命名
     *
     * @param str 字符串
     * @return 下劃線分隔的字符串
     */
    @NotNull
    public static String camelToUnderline(@Nullable String str) {
        if (isEmpty(str)) {
            return "";
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 將下劃線分隔命名轉換為駝峰式命名
     *
     * @param str 字符串
     * @param firstCapital 首字母是否大寫
     * @return 駝峰式命名的字符串
     */
    @NotNull
    public static String underlineToCamel(@Nullable String str, boolean firstCapital) {
        if (isEmpty(str)) {
            return "";
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        boolean nextUpperCase = false;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    sb.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    sb.append(c);
                }
            }
        }
        
        if (firstCapital) {
            return capitalize(sb.toString());
        }
        return sb.toString();
    }
}