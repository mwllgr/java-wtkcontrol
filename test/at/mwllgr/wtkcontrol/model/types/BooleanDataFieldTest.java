package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.model.DataField;
import org.junit.*;

import static org.junit.Assert.*;

public class BooleanDataFieldTest {
    DataField mainField;
    BooleanDataField booleanField;

    public BooleanDataFieldTest() {
        mainField = new DataField("Bool field", "0.00", 0x00, 0x01, DataFieldType.UNSIGNED_CHAR, 0, 1);
        booleanField = new BooleanDataField(mainField);
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
    public void testSetBooleanWorks() {
        booleanField.setValue(true);
        assertTrue(booleanField.getValue());
        booleanField.setValue(false);
        assertFalse(booleanField.getValue());
    }

    /**
     * Test for getBytes method.
     */
    @Test
    public void testGetBytesWorks() {
        booleanField.setValue(true);
        assertEquals(1, booleanField.getBytes()[0]);
        assertEquals(1, booleanField.getBytes().length);
        booleanField.setValue(false);
        assertEquals(0, booleanField.getBytes()[0]);
        assertEquals(1, booleanField.getBytes().length);
    }

    /**
     * Test for setBytes method.
     */
    @Test
    public void testSetBytesWorks() {
        booleanField.setBytes(new byte[]{0x01});
        assertTrue(booleanField.getValue());
        booleanField.setBytes(new byte[]{0x00});
        assertFalse(booleanField.getValue());
    }

    /**
     * Test for setValueFromString with valid data.
     */
    @Test
    public void testFromStringWithValidDataWorks() {
        booleanField.setValueFromString("1");
        assertTrue(booleanField.getValue());
        booleanField.setValueFromString("0");
        assertFalse(booleanField.getValue());
    }

    /**
     * Test for setValueFromString method with invalid data.
     */
    @Test
    public void testFromStringWithInvalidDataShouldntWork() {
        // Only 0/1 allowed.
        assertFalse(booleanField.setValueFromString("true"));
        assertFalse(booleanField.setValueFromString("false"));
        assertFalse(booleanField.setValueFromString("-1"));
        assertFalse(booleanField.setValueFromString("2"));
        assertFalse(booleanField.setValueFromString(""));
        assertFalse(booleanField.setValueFromString("yes"));
        assertFalse(booleanField.setValueFromString("no"));
    }
}
