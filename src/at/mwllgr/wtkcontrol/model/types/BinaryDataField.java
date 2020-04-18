package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.model.DataField;

import java.nio.ByteBuffer;

public class BinaryDataField extends DataField {
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

    public int setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            this.setValue(bytes[0]);
            return this.getValue();
        }

        throw new IllegalArgumentException("Invalid byte length for data type!");
    }

    @Override
    public String toString() {
        return String.format("%8s", Integer.toBinaryString(this.getValue())).replace(' ', '0');
    }
}
