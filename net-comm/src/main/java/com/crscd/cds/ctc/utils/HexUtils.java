package com.crscd.cds.ctc.utils;

import java.util.Arrays;

/**
 * @author zhaole
 * @date 2022-04-17
 */
public class HexUtils {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3 - 1];
        Arrays.fill(hexChars, ' ');

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = HEX_ARRAY[v >>> 4];
            hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
