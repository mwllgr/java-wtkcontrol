package at.mwllgr.wtkcontrol.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CRC16Test {
    public CRC16Test() {
    }

    /**
     * Test for valid write ACK CRC-16 checksum.
     */
    @Test
    public void testIsValidWriteAckCRC() {
        byte[] writeAck = new byte[]{0x00, 0x17, 0x00};
        assertArrayEquals(new byte[]{0x72, 0x00}, CRC16.calculate(writeAck));
    }

    /**
     * Test for valid sent command (sync time/date).
     */
    @Test
    public void testIsValidSyncDateTimeCommandCRC() {
        byte[] sentCommand = new byte[]{0x01, 0x14, 0x00, 0x00, 0x1B, 0x1E, 0x0C, 0x16, 0x02, 0x13};
        assertArrayEquals(new byte[]{(byte) 0xAF, (byte) 0x8D}, CRC16.calculate(sentCommand));
    }

    /**
     * Test for valid read CRC-16 checksum.
     */
    @Test
    public void testIsValidReadCRC() {
        byte[] writeAck = new byte[]{0x01, 0x15, 0x00, 0x00, 0x00, 0x02};
        assertArrayEquals(new byte[]{(byte) 0xFE, 0x17}, CRC16.calculate(writeAck));
    }
}
