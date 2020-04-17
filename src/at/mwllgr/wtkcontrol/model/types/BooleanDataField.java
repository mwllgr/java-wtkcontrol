package at.mwllgr.wtkcontrol.model.types;

public class BooleanDataField {
    private static int LENGTH = 1;
    private boolean value;

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

    public boolean setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            if (bytes[0] == 0) {
                this.setValue(false);
            } else if (bytes[0] == 1) {
                this.setValue(false);
            } else {
                return false;
            }

            return true;
        }

        throw new IllegalArgumentException("Invalid byte length for data type!");
    }

    @Override
    public String toString() {
        return this.getValue() ? "1" : "0";
    }
}
