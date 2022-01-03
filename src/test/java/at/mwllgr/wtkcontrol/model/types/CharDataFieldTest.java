package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.model.DataField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CharDataFieldTest {
    DataField mainField;
    CharDataField charField;

    public CharDataFieldTest() {
        mainField = new DataField("Char field", "0.00", 0x00, 0x01, DataFieldType.UNSIGNED_CHAR, 0, 250);
        charField = new CharDataField(mainField);
    }

    /**
     * Test for normal setValue method.
     */
    @Test
    public void testFromStringWithValidDataWorks() {
        charField.setValueFromString("121");
        assertEquals(121, charField.getValue());
        assertEquals("121", charField.toString());
        charField.setValueFromString("250");
        assertEquals(250, charField.getValue());
        assertEquals("250", charField.toString());
        charField.setValueFromString("4");
        assertEquals(4, charField.getValue());
        assertEquals("4", charField.toString());
        charField.setValueFromString("0");
        assertEquals(0, charField.getValue());
        assertEquals("0", charField.toString());
    }

    /**
     * Test for setValueFromString method with invalid data.
     */
    @Test
    public void testFromStringWithInvalidDataShouldNotWork() {
        assertFalse(charField.setValueFromString("251"));
        assertFalse(charField.setValueFromString("512"));
        assertFalse(charField.setValueFromString("1024"));
        assertFalse(charField.setValueFromString("256"));
        assertFalse(charField.setValueFromString("-1"));
        assertFalse(charField.setValueFromString("c"));
    }

    /**
     * Test for getBytes method.
     */
    @Test
    public void testGetBytesWorks() {
        charField.setValue(0);
        assertEquals(0, charField.getBytes()[0]);
        assertEquals(1, charField.getBytes().length);
        charField.setValue(95);
        assertEquals(95, charField.getBytes()[0]);
        assertEquals(1, charField.getBytes().length);
        charField.setValue(123);
        assertEquals(123, charField.getBytes()[0]);
        assertEquals(1, charField.getBytes().length);
        charField.setValueFromString("5");
        assertEquals(5, charField.getBytes()[0]);
        assertEquals(1, charField.getBytes().length);
    }

    /**
     * Test for setBytes method.
     */
    @Test
    public void testSetBytesWorks() {
        charField.setBytes(new byte[]{0x01});
        assertEquals(1, charField.getValue());
        charField.setBytes(new byte[]{0x6A});
        assertEquals(0x6A, charField.getValue());
    }

    /**
     * Test for toString() method.
     */
    @Test
    public void testToStringCorrectData() {
        charField.setBytes(new byte[]{0x01});
        assertEquals("1", charField.toString());
        charField.setBytes(new byte[]{(byte) 0xFA});
        assertEquals("250", charField.toString());
    }
}
