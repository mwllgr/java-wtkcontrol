package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.model.DataField;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

/**
 * Type:    Float (f)
 * Min:     3.4E-38 (?)
 * Max:     3.4E+38 (?)
 * Bytes:   4
 * <p>
 * Example: 23.5
 */
public class FloatDataField extends DataField {
    private static final int LENGTH = 4;
    private float value;

    public FloatDataField(DataField dataField) {
        super(dataField.getName(), dataField.getMenuEntry(), dataField.getAddress(),
                dataField.getLength(), dataField.getType(), dataField.getMin(),
                dataField.getMax(), dataField.isReadOnly());
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            float converted = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            this.setValue(converted);
        } else {
            throw new IllegalArgumentException("Invalid byte length for data type!");
        }
    }

    public boolean setValueFromString(String floatString) {
        float newValue;
        try {
            newValue = Float.parseFloat(floatString);
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
        // Little Endian byte order required
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(this.getValue()).array();
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%.1f", this.getValue());
    }
}
