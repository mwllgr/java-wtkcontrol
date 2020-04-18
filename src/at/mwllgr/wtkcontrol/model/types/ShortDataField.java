package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.model.DataField;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.LocalDate;

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

    public int setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            this.setValue(new BigInteger(bytes).intValue());
            return this.getValue();
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
