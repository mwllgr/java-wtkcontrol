package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.model.DataField;
import at.mwllgr.wtkcontrol.model.TimeWith24;

/**
 * Type:    Time (t)
 * Min:     00:00:00
 * Max:     24:59:59
 * Bytes:   3
 * <p>
 * Example: 23:00:00
 */
public class TimeDataField extends DataField {
    private static final int LENGTH = 3;
    private TimeWith24 value;

    public TimeDataField(DataField dataField) {
        super(dataField.getName(), dataField.getMenuEntry(), dataField.getAddress(),
                dataField.getLength(), dataField.getType(), dataField.getMin(),
                dataField.getMax(), dataField.isReadOnly());
    }

    public TimeWith24 getValue() {
        return value;
    }

    public void setValue(TimeWith24 value) {
        this.value = value;
    }

    public boolean setValueFromString(String time) {
        TimeWith24 timeObj = new TimeWith24();
        boolean success = timeObj.fromString(time);
        this.setValue(timeObj);
        return success;
    }

    public TimeWith24 setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            TimeWith24 time = new TimeWith24(bytes[2], bytes[1], bytes[0]);
            this.setValue(time);
            return this.getValue();
        }

        throw new IllegalArgumentException("Invalid byte length for data type!");
    }

    public byte[] getBytes() {
        return new byte[] {
                Integer.valueOf(this.getValue().getSecond()).byteValue(),
                Integer.valueOf(this.getValue().getMinute()).byteValue(),
                Integer.valueOf(this.getValue().getHour()).byteValue()
        };
    }

    @Override
    public String toString() {
        return this.getValue().toString();
    }
}
