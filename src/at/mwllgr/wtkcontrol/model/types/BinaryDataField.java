package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.model.DataField;

import java.math.BigInteger;

/**
 * Type:    Binary (b)
 * Min:     0
 * Max:     255
 * Bytes:   1
 * <p>
 * Example: 11010100
 */
public class BinaryDataField extends DataField {
    private static final int BINARY_RADIX = 2;
    private static final int LENGTH = 1;
    private int value;

    public BinaryDataField(DataField dataField) {
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

    public byte[] getBytes() {
        return new byte[] {
                Integer.valueOf(this.getValue()).byteValue()
        };
    }

    public void setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            // Prepend 00 to force positive signing
            byte[] unsignedBytes = new byte[]{0x00, bytes[0]};
            this.setValue(new BigInteger(unsignedBytes).intValue());
        } else {
            throw new IllegalArgumentException("Invalid byte length for data type!");
        }

    }

    public boolean setValueFromString(String binaryString) {
        if(binaryString.length() <= 8) {
            int newValue = Integer.parseInt(binaryString, BINARY_RADIX);
            this.setValue(newValue);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        // Print as 8-bit string
        return String.format("%8s", Integer.toBinaryString(this.getValue())).replace(' ', '0');
    }
}
