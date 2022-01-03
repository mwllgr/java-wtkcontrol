package at.mwllgr.wtkcontrol.helpers;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Class for CRC calculation.
 *
 * The heating controller uses CRC16_BUYPASS.
 *
 * CRC-Order:               16
 * Input type:              Hex
 * Polynomial:              0x8005
 * Initial value:           0x0
 * LSB/Final Xor Value:     0x0
 * Input/data reflected:    No
 * Result reflected:        No
 *
 * Source for doCalculation:
 * https://github.com/NordicSemiconductor/Android-BLE-Common-Library/blob/master/ble-common/src/main/java/no/nordicsemi/android/ble/common/util/CRC16.java
 */
public final class CRC16 {
    public static byte[] calculate(byte[] bytes) {
        byte[] calculated = BigInteger.valueOf(doCalculation(0x8005, 0x0000, bytes, 0x00, bytes.length, false, false, 0x0000)).toByteArray();
        return Arrays.copyOfRange(calculated, calculated.length - 2, calculated.length);
    }

    /**
     * Calculates the CRC over given range of bytes from the block of data with given polynomial and initial value.
     * This method may also reverse input bytes and reverse output CRC.
     *
     * See: http://www.zorc.breitbandkatze.de/crc.html
     *
     * @param poly   Polynomial used to calculate the CRC16.
     * @param init   Initial value to feed the buffer.
     * @param data   The input data block for computation.
     * @param offset Offset from where the range starts.
     * @param length Length of the range in bytes.
     * @param refin  True if the input data should be reversed.
     * @param refout True if the output data should be reversed.
     * @return CRC calculated with given parameters.
     */
    private static int doCalculation(final int poly, final int init, final byte[] data, final int offset, final int length, final boolean refin, final boolean refout, final int xorout) {
        int crc = init;

        for (int i = offset; i < offset + length && i < data.length; ++i) {
            final byte b = data[i];
            for (int j = 0; j < 8; j++) {
                final int k = refin ? 7 - j : j;
                final boolean bit = ((b >> (7 - k) & 1) == 1);
                final boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= poly;
            }
        }

        if (refout) {
            return (Integer.reverse(crc) >>> 16) ^ xorout;
        } else {
            return (crc ^ xorout) & 0xFFFF;
        }
    }
}
