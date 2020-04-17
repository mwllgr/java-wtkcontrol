package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.model.DataField;

import java.nio.ByteBuffer;
import java.time.LocalDate;

public class ShortDataField extends DataField {
    private static final int LENGTH = 2;
    private int value;

    public ShortDataField(String name, String menuEntry, int address, int length, DataFieldType type, float min, float max, boolean readOnly) {
        super(name, menuEntry, address, length, type, min, max, readOnly);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public LocalDate setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            ByteBuffer wrapped = ByteBuffer.wrap(bytes);
            this.setValue(wrapped.getInt());
        }

        throw new IllegalArgumentException("Invalid byte length for data type!");
    }

    public byte[] getBytes() {
        return new byte[] {
                Integer.valueOf(this.getValue()).byteValue()
        };
    }

    @Override
    public String toString() {
        return Integer.toString(this.getValue());
    }
}
