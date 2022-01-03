package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.model.DataField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DateDataFieldTest {
    DataField mainField;
    DateDataField dateField;

    public DateDataFieldTest() {
        mainField = new DataField("Date field", "0.00", 0x00, 0x03, DataFieldType.DATE);
        dateField = new DateDataField(mainField);
    }

    /**
     * Test for getBytes method.
     */
    @Test
    public void testGetBytesWorks() {
        dateField.setBytes(new byte[]{0x02, 0x0B, 0x10});
        assertArrayEquals(dateField.getBytes(), new byte[]{0x02, 0x0B, 0x10});
    }

    /**
     * Test for setBytes method.
     */
    @Test
    public void testSetBytesWorks() {
        dateField.setBytes(new byte[]{0x08, 0x02, 0x14});
        assertEquals(8, dateField.getValue().getDayOfMonth());
        assertEquals(2, dateField.getValue().getMonthValue());
        assertEquals(20, dateField.getValue().getYear());
        dateField.setBytes(new byte[]{0x01, 0x01, 0x01});
        assertEquals(1, dateField.getValue().getDayOfMonth());
        assertEquals(1, dateField.getValue().getMonthValue());
        assertEquals(1, dateField.getValue().getYear());
    }

    /**
     * Test for toString() method.
     */
    @Test
    public void testToStringCorrectData() {
        dateField.setBytes(new byte[]{0x09, 0x04, 0x10});
        assertEquals("09.04.16", dateField.toString());
        dateField.setBytes(new byte[]{0x01, 0x01, 0x01});
        assertEquals("01.01.01", dateField.toString());
    }
}
