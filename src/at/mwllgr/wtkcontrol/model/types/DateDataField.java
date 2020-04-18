package at.mwllgr.wtkcontrol.model.types;

import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.model.DataField;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateDataField extends DataField {
    private static final int LENGTH = 3;
    private LocalDate value;

    public DateDataField(DataField dataField) {
        super(dataField.getName(), dataField.getMenuEntry(), dataField.getAddress(),
                dataField.getLength(), dataField.getType(), dataField.getMin(),
                dataField.getMax(), dataField.isReadOnly());
    }

    public LocalDate getValue() {
        return value;
    }

    public void setValue(LocalDate value) {
        this.value = value;
    }

    public LocalDate setBytes(byte[] bytes) {
        if(bytes.length == LENGTH) {
            this.setValue(LocalDate.of(bytes[2], bytes[1], bytes[0]));
            return this.getValue();
        }

        throw new IllegalArgumentException("Invalid byte length for data type!");
    }

    public byte[] getBytes() {
        DateFormat twoDigitYearFmt = new SimpleDateFormat("yy");

        return new byte[] {
                Integer.valueOf(this.getValue().getDayOfMonth()).byteValue(),
                Integer.valueOf(this.getValue().getMonthValue()).byteValue(),
                Integer.valueOf(Integer.parseInt(twoDigitYearFmt.format(this.getValue()))).byteValue()
        };
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        return this.getValue().format(formatter);
    }
}
