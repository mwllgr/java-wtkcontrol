package at.mwllgr.wtkcontrol.model.types;

public class BooleanDataField {
    private boolean value;

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean setBytes(byte[] bytes) {
        if(bytes[0] == 0) {
            this.setValue(false);
        }
        else if(bytes[0] == 1) {
            this.setValue(false);
        }
        else
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return this.getValue() ? "1" : "0";
    }
}
