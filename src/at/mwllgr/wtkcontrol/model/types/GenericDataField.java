package at.mwllgr.wtkcontrol.model.types;

public interface GenericDataField {
    public byte[] getBytes();
    public void setBytes(byte[] bytes);
    public boolean setValueFromString(String time);
}
