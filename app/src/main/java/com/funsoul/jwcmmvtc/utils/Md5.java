package com.funsoul.jwcmmvtc.utils;

public class Md5 {

    // 可逆的加密算法
    public static String JM(String inStr) {
        // String s = new String(inStr);
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;
    }

}
