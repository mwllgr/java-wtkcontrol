package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.model.DataField;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FloatDataField extends DataField {
    private static int LENGTH = 4;
    private float value;

    public FloatDataField(String name, String menuEntry, int address, int length, DataFieldType type, float min, float max, boolean readOnly) {
        super(name, menuEntry, address, length, type, min, max, readOnly);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            float converted = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            this.setValue(converted);
            return converted;
        }

        throw new IllegalArgumentException("Invalid byte length for data type!");
    }

    public float getBytes() {
        byte[] bytes = { };
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    @Override
    public String toString() {
        return Float.toString(this.getValue());
    }
}
