package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.model.DataField;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Type:    Short (n)
 * Min:     0
 * Max:     65535
 * Bytes:   2
 * <p>
 * Example: 34836
 */
public class ShortDataField extends DataField {
    private static final int LENGTH = 2;
    private int value;

    public ShortDataField(DataField dataField) {
        super(dataField.getName(), dataField.getMenuEntry(), dataField.getAddress(),
                dataField.getLength(), dataField.getType(), dataField.getMin(),
                dataField.getMax(), dataField.isReadOnly());
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            byte[] unsignedShortArr = new byte[1 + bytes.length];
            unsignedShortArr[0] = 0x00;
            System.arraycopy(bytes, 0, unsignedShortArr, 1, bytes.length);
            this.setValue(new BigInteger(unsignedShortArr).intValue());
        } else {
            throw new IllegalArgumentException("Invalid byte length for data type!");
        }
    }

    public boolean setValueFromString(String newValueStr) {
        int newValue = 0;
        try {
            newValue = Integer.parseInt(newValueStr);
        } catch (NumberFormatException ex) {
            return false;
        }

        if(newValue >= this.getMin() && newValue <= this.getMax()) {
            this.setValue(newValue);
            return true;
        }

        return false;
    }

    public byte[] getBytes() {
        return ByteBuffer.allocate(2).putChar((char) this.getValue()).array();
    }

    @Override
    public String toString() {
        return Integer.toString(this.getValue());
    }
}
