package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.model.DataField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Type:    Date (d)
 * Min:     01.01.00
 * Max:     31.12.99
 * Bytes:   3
 * <p>
 * Example: 25.04.20
 */
public class DateDataField extends DataField {
    private static final int LENGTH = 3;
    private LocalDate value;

    public DateDataField(DataField dataField) {
        super(dataField.getName(), dataField.getMenuEntry(), dataField.getAddress(),
                dataField.getLength(), dataField.getType(), dataField.getMin(),
                dataField.getMax(), dataField.isReadOnly());
    }

    public LocalDate getValue() {
        return value;
    }

    public void setValue(LocalDate value) {
        this.value = value;
    }

    public void setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            this.setValue(LocalDate.of(bytes[2], bytes[1], bytes[0]));
        } else {
            throw new IllegalArgumentException("Invalid byte length for data type!");
        }
    }

    public boolean setValueFromString(String time) {
        return false;
    }

    public byte[] getBytes() {
        return new byte[] {
                Integer.valueOf(this.getValue().getDayOfMonth()).byteValue(),
                Integer.valueOf(this.getValue().getMonthValue()).byteValue(),
                // Get 2-digit year
                Integer.valueOf(Integer.parseInt(this.getValue().format(DateTimeFormatter.ofPattern("yy")))).byteValue()
        };
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        return this.getValue().format(formatter);
    }
}
