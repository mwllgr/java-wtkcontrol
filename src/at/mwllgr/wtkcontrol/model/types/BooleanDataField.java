package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.model.DataField;

public class BooleanDataField extends DataField {
    private static final int LENGTH = 1;
    private boolean value;

    public BooleanDataField(DataField dataField) {
        super(dataField.getName(), dataField.getMenuEntry(), dataField.getAddress(),
                dataField.getLength(), dataField.getType(), dataField.getMin(),
                dataField.getMax(), dataField.isReadOnly());
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public byte[] getBytes() {
        byte value = 0;

        if(this.getValue()) {
            value = 1;
        }

        return new byte[]{value};
    }

    public void setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            if (bytes[0] == 0) {
                this.setValue(false);
            } else if (bytes[0] == 1) {
                this.setValue(false);
            }
        }
        else
        {
            throw new IllegalArgumentException("Invalid byte length for data type!");
        }
    }

    public boolean setValueFromString(String time) {
        return false;
    }

    @Override
    public String toString() {
        return this.getValue() ? "1" : "0";
    }
}
