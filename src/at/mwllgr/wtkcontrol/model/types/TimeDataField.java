package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.controller.Tools;
import at.mwllgr.wtkcontrol.model.DataField;
import at.mwllgr.wtkcontrol.model.TimeWith24;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
                Integer.valueOf(this.getValue().getSeconds()).byteValue(),
                Integer.valueOf(this.getValue().getMinutes()).byteValue(),
                Integer.valueOf(this.getValue().getHours()).byteValue()
        };
    }

    @Override
    public String toString() {
        return this.getValue().toString();
    }
}
