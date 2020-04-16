package at.mwllgr.wtkcontrol.controller;

import at.mwllgr.wtkcontrol.globals.DataFieldType;

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

    public static String getByteArrayAsHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte currByte : bytes) {
            sb.append(String.format("%02X ", currByte));
        }
        return sb.toString().trim();
    }

    /**
     * Calculates the CRC-16_BUYPASS.
     * Source: https://introcs.cs.princeton.edu/java/61data/CRC16CCITT.java
     * @param bytes Bytes to calculate the CRC of
     * @return CRC-16_BUYPASS
     */
    public static byte[] getCrc16(byte[] bytes) {
        int crc = 0x0000; // Initial value
        int polynomial = 0x8005;

        for (byte currByte : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((currByte   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }

        return BigInteger.valueOf(crc).toByteArray();
    }
}
