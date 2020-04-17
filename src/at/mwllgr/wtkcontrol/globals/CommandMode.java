package at.mwllgr.wtkcontrol.globals;

/**
 * Contains the available command modes (PC -> Heating controller)
 */
public final class CommandMode {
    private CommandMode() { }

    public static final byte[] WRITE_MEMORY = { 0x13 };
    public static final byte[] WRITE_DATETIME = { 0x14 };
    public static final byte[] READ_MEMORY = { 0x15 };
    public static final byte[] LOGGER = { 0x18 }; // Unknown use-case
    public static final byte[] WAKEUP = { 0x41, 0x54, 0x0D }; // Needed when unit doesn't respond
}
