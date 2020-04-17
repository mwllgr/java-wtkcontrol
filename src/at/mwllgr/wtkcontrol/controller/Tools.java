package at.mwllgr.wtkcontrol.controller;

import java.math.BigInteger;

public final class Tools {
    private Tools() { }

    public static byte[] hexStringToByteArray(String input) {
        int len = input.length();

        if (len == 0) {
            return new byte[] {};
        }

        byte[] data;
        int startIdx;
        if (len % 2 != 0) {
            data = new byte[(len / 2) + 1];
            data[0] = (byte) Character.digit(input.charAt(0), 16);
            startIdx = 1;
        } else {
            data = new byte[len / 2];
            startIdx = 0;
        }

        for (int i = startIdx; i < len; i += 2) {
            data[(i + 1) / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4)
                    + Character.digit(input.charAt(i+1), 16));
        }
        return data;
    }

    public static String getByteArrayAsHexString(byte[] bytes, boolean spaces) {
        StringBuilder sb = new StringBuilder();
        for (byte currByte : bytes) {
            sb.append(String.format("%02X", currByte));
            if(spaces) sb.append(" ");
        }
        return sb.toString().trim();
    }

    public static int hexStringToInt(String hex) {
        return Integer.parseInt(hex, 16);
    }

    public static String intToHexString(int hex) {
        return String.format("%02X", hex);
    }
}
