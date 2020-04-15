package at.mwllgr.wtkcontrol.globals;

public abstract class CommandModes {
    static final byte WRITE_MEMORY = 0x13;
    static final byte WRITE_DATETIME = 0x14;
    static final byte READ_MEMORY = 0x15;
    static final byte LOGGER = 0x18; // Unknown use-case
}
