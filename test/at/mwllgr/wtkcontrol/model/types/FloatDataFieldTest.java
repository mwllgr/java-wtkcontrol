package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.model.DataField;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FloatDataFieldTest {
    DataField mainField;
    FloatDataField floatField;

    public FloatDataFieldTest() {
        mainField = new DataField("Float field", "0.00", 0x00, 0x04, DataFieldType.FLOAT, 0.0f, 60.0f);
        floatField = new FloatDataField(mainField);
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
        floatField.setValueFromString("28.3");
        assertEquals(28.3, floatField.getValue(), 0.1);
        assertEquals("28.3", floatField.toString());
    }

    /**
     * Test for setValueFromString method with invalid data.
     */
    @Test
    public void testFromStringWithInvalidDataShouldNotWork() {
        assertFalse(floatField.setValueFromString(""));
        assertFalse(floatField.setValueFromString("float"));
    }

    /**
     * Test for setBytes method.
     */
    @Test
    public void testSetBytesWorks() {
        floatField.setBytes(new byte[]{0x00, 0x00, (byte) 0xC8, 0x41});
        assertEquals(25.0, floatField.getValue(), 0.1);
    }

    /**
     * Test for toString() method.
     */
    @Test
    public void testToStringCorrectData() {
        floatField.setBytes(new byte[]{0x00, 0x00, (byte) 0xC8, 0x41});
        assertEquals("25.0", floatField.toString());
    }
}
