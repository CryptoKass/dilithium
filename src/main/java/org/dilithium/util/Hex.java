package org.dilithium.util;

public class Hex {
    private static final char[] DIGITS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    public static String getHex(byte[] data) {
        final int l = data.length;
        final char[] outData = new char[l << 1];
        for(int i = 0, j = 0; i < l; i++) {
            outData[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            outData[j++] = DIGITS[(0x0F & data[i])];
        }

        return new String(outData);
    }

    public static byte[] fromHex(String s) {
        if(s.startsWith("0x")) {
            s = s.substring(2);
        }

        if(s.length() % 2 != 0) {
            s = "0" + s;
        }

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String getReverseHex(byte[] data) {
        return getHex(reverse(data));
    }

    private static byte[] reverse(byte[] data) {
        byte tmp;

        for(int i = 0, j = data.length - 1; j > i; i++, j--) {
            tmp = data[j];
            data[j] = data[i];
            data[i] = tmp;
        }

        return data;
    }
}
