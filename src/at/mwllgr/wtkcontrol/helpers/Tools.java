package at.mwllgr.wtkcontrol.helpers;

/**
 * This class contains neat functions that are used throughout the program.
 */
public final class Tools {
    private Tools() { }

    /**
     * Converts a hex input string to a byte array
     * @param input Hex input string
     * @return Converted bytes
     */
    public static byte[] hexStringToByteArray(String input) {
        // Source: https://stackoverflow.com/a/39970426
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

    /**
     * Converts a byte array into a hex string.
     * @param bytes Bytes to convert
     * @param spaces true = space after each group
     * @return Byte array as hexadecimal string
     */
    public static String getByteArrayAsHexString(byte[] bytes, boolean spaces) {
        StringBuilder sb = new StringBuilder();
        for (byte currByte : bytes) {
            sb.append(String.format("%02X", currByte));
            if(spaces) sb.append(" ");
        }

        return sb.toString().trim();
    }

    /**
     * Converts a hex string into an int.
     * @param hex String to convert
     * @return Converted integer
     */
    public static int hexStringToInt(String hex) {
        return Integer.parseInt(hex, 16);
    }

    /**
     * Converts an int into a hex string.
     * @param hex int to convert
     * @return int as hexadecimal string
     */
    public static String intToHexString(int hex) {
        return String.format("%02X", hex);
    }
}
