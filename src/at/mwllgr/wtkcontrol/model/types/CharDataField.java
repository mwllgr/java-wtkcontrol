package at.mwllgr.wtkcontrol.model.types;

import java.nio.ByteBuffer;
import java.time.LocalDate;

public class CharDataField {
    private static final int LENGTH = 1;
    private int value;

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
