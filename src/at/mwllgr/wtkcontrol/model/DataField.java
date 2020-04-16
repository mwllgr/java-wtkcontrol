package at.mwllgr.wtkcontrol.model;

import at.mwllgr.wtkcontrol.controller.Tools;
import at.mwllgr.wtkcontrol.globals.DataFieldType;

public class DataField {
    String name;
    String menuEntry;
    byte[] address;
    byte[] length;
    DataFieldType type;
    double min = 0.00;
    double max = 0.00;
    boolean readOnly = false;

    public DataField(String name, String menuEntry, byte[] address, byte[] length, DataFieldType type) {
        this.setName(name);
        this.setMenuEntry(menuEntry);
        this.setAddress(address);
        this.setLength(length);
        this.setType(type);
    }

    public DataField(String name, String menuEntry, byte[] address, byte[] length, DataFieldType type, double min, double max) {
        this(name, menuEntry, address, length, type);
        this.setMin(min);
        this.setMax(max);
    }

    public DataField(String name, String menuEntry, byte[] address, byte[] length, DataFieldType type, double min, double max, boolean readOnly) {
        this(name, menuEntry, address, length, type, min, max);
        this.setReadOnly(readOnly);
    }

    @Override
    public String toString() {
        return this.getName() + " (M: " + this.getMenuEntry()
                + ", A: " + Tools.getByteArrayAsHexString(this.getAddress(), true)
                + ", L: " + Tools.getByteArrayAsHexString(this.getLength(), true)
                + ", T: " + this.getType().toString() + ")";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenuEntry() {
        return menuEntry;
    }

    public void setMenuEntry(String menuEntry) {
        this.menuEntry = menuEntry;
    }

    public byte[] getAddress() {
        return address;
    }

    public void setAddress(byte[] address) {
        this.address = address;
    }

    public byte[] getLength() {
        return length;
    }

    public void setLength(byte[] length) {
        this.length = length;
    }

    public DataFieldType getType() {
        return type;
    }

    public void setType(DataFieldType type) {
        this.type = type;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
