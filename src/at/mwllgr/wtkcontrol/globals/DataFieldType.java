package at.mwllgr.wtkcontrol.globals;

/**
 * Enum for available data types.
 * Added functions for easier parsing of single-letter codes (as used in the CSV file).
 */
public enum DataFieldType {
    BINARY("b"),
    FLOAT("f"),
    UNSIGNED_CHAR("c"),
    UNSIGNED_INT("n"),
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

        throw new IllegalArgumentException("No entry with text " + text + " found!");
    }
}
