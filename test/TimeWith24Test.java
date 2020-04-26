import at.mwllgr.wtkcontrol.model.TimeWith24;
import org.junit.*;

import static org.junit.Assert.*;

public class TimeWith24Test {
    public TimeWith24Test() {
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
     * Test for valid (working) time constructor with int parameters.
     */
    @Test
    public void testValidTimeConstructorWorks() {
        try {
            TimeWith24 validTime = new TimeWith24(22, 30, 0);
        } catch (IllegalArgumentException ex) {
            fail("Expected valid time range");
        }
    }

    /**
     * Test for invalid time constructor with int parameters.
     */
    @Test
    public void testInvalidTimeConstructorShouldThrowException() {
        try {
            TimeWith24 invalidTime = new TimeWith24(26, -3, 34);
            fail("Expected valid time range");
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Time range invalid.");
        }
    }

    /**
     * Test for valid time with .fromString() method.
     */
    @Test
    public void testValidTimeFromStringWorks() {
        TimeWith24 validTime = new TimeWith24();
        String[] testTimes = new String[]{
                "19:30:55",
                "00:00:00",
                "24:00:00",
                "24:59:59"
        };

        for (String time : testTimes) {
            assertTrue(validTime.fromString(time));
            assertEquals(validTime.toString(), time);
        }
    }

    /**
     * Test for invalid time with .fromString() method.
     */
    @Test
    public void testInvalidTimeFromStringShouldFail() {
        TimeWith24 validTime = new TimeWith24();
        String[] testTimes = new String[]{
                "",
                "time",
                "00",
                "24",
                "24;50:60",
                "00:60:60",
                "12:60:00",
                "-12:30:25",
                "00.00.00"
        };

        for (String time : testTimes) {
            assertFalse(validTime.fromString(time));
            assertNotEquals(validTime.toString(), time);
        }
    }

    /**
     * Test for valid time with .set* methods.
     */
    @Test
    public void testValidTimeFromMethodsWork() {
        TimeWith24 validTime = new TimeWith24();
        int[] hours = new int[]{0, 1, 7, 12, 13, 24};
        int[] minutesSeconds = new int[]{0, 1, 7, 12, 13, 24, 35, 59};

        for (int hour : hours) {
            assertTrue(validTime.setHour(hour));
            assertEquals(validTime.getHour(), hour);
        }

        for (int value : minutesSeconds) {
            assertTrue(validTime.setMinute(value));
            assertEquals(validTime.getMinute(), value);
            assertTrue(validTime.setSecond(value));
            assertEquals(validTime.getSecond(), value);
        }
    }

    /**
     * Test for invalid time with .set* methods.
     */
    @Test
    public void testInvalidTimeFromMethodsShouldFail() {
        TimeWith24 validTime = new TimeWith24();
        int[] hours = new int[]{-1, 25, 48};
        int[] minutesSeconds = new int[]{-1, 60, 100};

        for (int hour : hours) {
            assertFalse(validTime.setHour(hour));
            assertNotEquals(validTime.getHour(), hour);
        }

        for (int value : minutesSeconds) {
            assertFalse(validTime.setMinute(value));
            assertNotEquals(validTime.getMinute(), value);
            assertFalse(validTime.setSecond(value));
            assertNotEquals(validTime.getSecond(), value);
        }
    }

    /**
     * Test for correct toString() values for valid time.
     */
    @Test
    public void testValidTimeCorrectToStringValue() {
        TimeWith24 validTime = new TimeWith24(1, 30, 56);
        assertEquals(validTime.toString(), "01:30:56");
        validTime = new TimeWith24(13, 7, 59);
        assertEquals(validTime.toString(), "13:07:59");
        validTime.fromString("24:00:00");
        assertEquals(validTime.toString(), "24:00:00");
        validTime.setSecond(59);
        assertEquals(validTime.toString(), "24:00:59");
        validTime.fromString("00:00:00");
        assertEquals(validTime.toString(), "00:00:00");
    }
}
