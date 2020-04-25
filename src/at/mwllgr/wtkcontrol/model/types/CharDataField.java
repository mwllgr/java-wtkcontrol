package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.model.DataField;

import java.math.BigInteger;

/**
 * Type:    Char (c)
 * Min:     0
 * Max:     255
 * Bytes:   1
 * <p>
 * Example: 236
 */
public class CharDataField extends DataField {
    private static final int LENGTH = 1;
    private int value;

    public CharDataField(DataField dataField) {
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
            // Prepend 00 to force positive signing
            byte[] unsignedBytes = new byte[]{0x00, bytes[0]};
            this.setValue(new BigInteger(unsignedBytes).intValue());
        } else {
            throw new IllegalArgumentException("Invalid byte length for data type!");
        }
    }

    public boolean setValueFromString(String charStr) {
        int newValue;
        try {
            newValue = Integer.parseInt(charStr);
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
        return new byte[] {
                Integer.valueOf(this.getValue()).byteValue()
        };
    }

    @Override
    public String toString() {
        return Integer.toString(this.getValue());
    }
}
