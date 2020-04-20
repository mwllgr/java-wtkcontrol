package at.mwllgr.wtkcontrol.model;

import at.mwllgr.wtkcontrol.controller.Tools;
import at.mwllgr.wtkcontrol.globals.DataFieldType;
import javafx.beans.property.SimpleStringProperty;

public class DataField {
    private final SimpleStringProperty name;
    private SimpleStringProperty toString;
    String menuEntry;
    int address; // Offset
    int length;
    DataFieldType type;
    float min = 0.00f;
    float max = 0.00f;
    boolean readOnly = false;

    public DataField(String name, String menuEntry, int address, int length, DataFieldType type) {
        this.name = new SimpleStringProperty(name);
        this.setMenuEntry(menuEntry);
        this.setAddress(address);
        this.setLength(length);
        this.setType(type);
    }

    public DataField(String name, String menuEntry, int address, int length, DataFieldType type, float min, float max) {
        this(name, menuEntry, address, length, type);
        this.setMin(min);
        this.setMax(max);
    }

    public DataField(String name, String menuEntry, int address, int length, DataFieldType type, float min, float max, boolean readOnly) {
        this(name, menuEntry, address, length, type, min, max);
        this.setReadOnly(readOnly);
    }

    @Override
    public String toString() {
        return this.getName() + " (M: " + this.getMenuEntry()
                + ", A: " + Tools.intToHexString(this.getAddress())
                + ", L: " + Tools.intToHexString(this.getLength())
                + ", T: " + this.getType().toString() + ")";
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getToString() {
        return toString.get();
    }

    public SimpleStringProperty toStringProperty() {
        this.toString = new SimpleStringProperty(this.toString());
        return toString;
    }

    public String getMenuEntry() {
        return menuEntry;
    }

    public void setMenuEntry(String menuEntry) {
        this.menuEntry = menuEntry;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public DataFieldType getType() {
        return type;
    }

    public void setType(DataFieldType type) {
        this.type = type;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
