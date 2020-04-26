package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.model.DataField;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ShortDataFieldTest {
    DataField mainField;
    ShortDataField shortField;

    public ShortDataFieldTest() {
        mainField = new DataField("Short field", "0.00", 0x00, 0x02, DataFieldType.UNSIGNED_SHORT, 0, 64300);
        shortField = new ShortDataField(mainField);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test for normal setValue method.
     */
    @Test
    public void testFromStringWithValidDataWorks() {
        shortField.setValueFromString("59186");
        assertEquals(59186, shortField.getValue());
        assertEquals("59186", shortField.toString());
        shortField.setValueFromString("23918");
        assertEquals(23918, shortField.getValue());
        assertEquals("23918", shortField.toString());
        shortField.setValueFromString("4");
        assertEquals(4, shortField.getValue());
        assertEquals("4", shortField.toString());
        shortField.setValueFromString("0");
        assertEquals(0, shortField.getValue());
        assertEquals("0", shortField.toString());
    }

    /**
     * Test for setValueFromString method with invalid data.
     */
    @Test
    public void testFromStringWithInvalidDataShouldNotWork() {
        assertFalse(shortField.setValueFromString("65535"));
        assertFalse(shortField.setValueFromString("65000"));
        assertFalse(shortField.setValueFromString("-1"));
        assertFalse(shortField.setValueFromString("short"));
        assertFalse(shortField.setValueFromString("c"));
    }

    /**
     * Test for setBytes method.
     */
    @Test
    public void testSetBytesWorks() {
        shortField.setBytes(new byte[]{0x00, 0x02});
        assertEquals(2, shortField.getValue());
        shortField.setBytes(new byte[]{0x15, 0x0A});
        assertEquals(0x150A, shortField.getValue());
    }

    /**
     * Test for toString() method.
     */
    @Test
    public void testToStringCorrectData() {
        shortField.setBytes(new byte[]{0x15, 0x0A});
        assertEquals("5386", shortField.toString());
    }
}