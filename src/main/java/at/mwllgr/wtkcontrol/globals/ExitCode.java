package at.mwllgr.wtkcontrol.globals;

/**
 * Contains the available exit codes for no-GUI mode
 */
public final class ExitCode {
    private ExitCode() { }

    public static final short GENERAL_ERROR = 1;
    public static final short MISSING_PARAMETER = 10;
    public static final short SERIAL_ERROR = 11;
    public static final short FILE_ERROR = 12;
    public static final short FIELD_NOT_FOUND = 13;
    public static final short FIELD_READ_ONLY = 14;
    public static final short VALIDATION_ERROR = 15;
}
