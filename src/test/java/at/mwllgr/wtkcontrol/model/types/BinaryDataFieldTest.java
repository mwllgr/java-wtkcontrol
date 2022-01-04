package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.model.DataField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BinaryDataFieldTest {
    DataField mainField;
    BinaryDataField binaryField;

    public BinaryDataFieldTest() {
        mainField = new DataField("Binary field", "0.00", 0x00, 0x01, DataFieldType.BINARY);
        binaryField = new BinaryDataField(mainField);
    }

    /**
     * Test for normal setValue method.
     */
    @Test
    public void testFromStringWithValidDataWorks() {
        binaryField.setValueFromString("1111");
        assertEquals(15, binaryField.getValue());
        assertEquals("00001111", binaryField.toString());
        binaryField.setValueFromString("11111111");
        assertEquals(255, binaryField.getValue());
        assertEquals("11111111", binaryField.toString());
    }

    /**
     * Test for setValueFromString method with invalid data.
     */
    @Test
    public void testFromStringWithInvalidDataShouldNotWork() {
        assertFalse(binaryField.setValueFromString(""));
        assertFalse(binaryField.setValueFromString("2"));
        assertFalse(binaryField.setValueFromString("11120110"));
        assertFalse(binaryField.setValueFromString("-1"));
        assertFalse(binaryField.setValueFromString("+1"));
        assertFalse(binaryField.setValueFromString("binary"));
    }

    /**
     * Test for getBytes method.
     */
    @Test
    public void testGetBytesWorks() {
        binaryField.setValue(0);
        assertEquals(0, binaryField.getBytes()[0]);
        assertEquals(1, binaryField.getBytes().length);
        binaryField.setValue(1);
        assertEquals(1, binaryField.getBytes()[0]);
        assertEquals(1, binaryField.getBytes().length);
        binaryField.setValue(15);
        assertEquals(15, binaryField.getBytes()[0]);
        assertEquals(1, binaryField.getBytes().length);
        binaryField.setValueFromString("01101011");
        assertEquals(107, binaryField.getBytes()[0]);
        assertEquals(1, binaryField.getBytes().length);
    }

    /**
     * Test for setBytes method.
     */
    @Test
    public void testSetBytesWorks() {
        binaryField.setBytes(new byte[]{0x01});
        assertEquals(1, binaryField.getValue());
        binaryField.setBytes(new byte[]{0x6A});
        assertEquals(0x6A, binaryField.getValue());
    }

    /**
     * Test for toString() method.
     */
    @Test
    public void testToStringCorrectData() {
        binaryField.setBytes(new byte[]{0x01});
        assertEquals("00000001", binaryField.toString());
        binaryField.setBytes(new byte[]{0x6A});
        assertEquals("01101010", binaryField.toString());
    }
}
