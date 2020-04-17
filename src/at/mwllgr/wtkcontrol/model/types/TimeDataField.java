package at.mwllgr.wtkcontrol.model.types;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeDataField {
    private static int LENGTH = 3;
    private LocalTime value;

    public LocalTime getValue() {
        return value;
    }

    public void setValue(LocalTime value) {
        this.value = value;
    }

    public LocalTime setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            this.setValue(LocalTime.of(bytes[2], bytes[1], bytes[0]));
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return this.getValue().format(formatter);
    }
}