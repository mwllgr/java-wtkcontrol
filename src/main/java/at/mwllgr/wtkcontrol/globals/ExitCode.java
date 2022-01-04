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
}
