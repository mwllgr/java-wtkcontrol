package at.mwllgr.wtkcontrol.globals;

import at.mwllgr.wtkcontrol.helpers.WtkLogger;

/**
 * Enum for available data types.
 * Added functions for easier parsing of single-letter codes (as used in the CSV file).
 */
public enum DataFieldType {
    BINARY("b"),
    FLOAT("f"),
    UNSIGNED_CHAR("c"),
    UNSIGNED_SHORT("n"),
    TIME("t"),
    DATE("d");

    private final String text;

    DataFieldType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static DataFieldType fromString(String text) {
        for (DataFieldType type : DataFieldType.values()) {
            if (type.text.equalsIgnoreCase(text)) {
                return type;
            }
        }

        WtkLogger.getInstance().error("Invalid data type '" + text + "' in CSV address list!");
        System.exit(ExitCode.CSV_MALFORMED);
        return null;
    }
}
