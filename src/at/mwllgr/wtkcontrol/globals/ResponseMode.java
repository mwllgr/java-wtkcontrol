package at.mwllgr.wtkcontrol.globals;

/**
 * Contains the known response modes for received frames (Heating controller -> PC)
 */
public final class ResponseMode {
    private ResponseMode() { }

    public static final byte LOGGER = 0x03; // Unknown use-case
    public static final byte WRITE_RESPONSE = 0x11;
    public static final byte READ_RESPONSE = 0x17;
}
