package at.mwllgr.wtkcontrol.model;

import at.mwllgr.wtkcontrol.globals.DataFieldOffset;
import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.helpers.SerialHelper;
import at.mwllgr.wtkcontrol.helpers.Tools;
import at.mwllgr.wtkcontrol.helpers.WtkLogger;
import at.mwllgr.wtkcontrol.model.types.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 * Main repository class:
 * Responsible for data storage, contains SerialController object too.
 */
public class Repository {
    static final String CSV_SEPARATOR = ",";
    private final SerialHelper serialComm = SerialHelper.getInstance();
    private DataField newValue;
    private boolean noGuiMode = false;

    // Text areas in main GUI
    private final StringProperty txtRaw = new SimpleStringProperty();
    private final StringProperty txtRead = new SimpleStringProperty();
    private final StringProperty txtCrcCalc = new SimpleStringProperty();
    private final StringProperty txtCrc = new SimpleStringProperty();

    ObservableList<DataField> fields;
    // If parsedMaxBytesToRead is true, the first CSV line is skipped.
    boolean parsedMaxBytesToRead = false;
    byte[] bytesToRead; // Max bytes to read (all bytes)
    boolean loggerEnabled = false;

    // Singleton
    private static Repository instance;

    private Repository() {
        this.fields = FXCollections.observableList(new LinkedList<>());
    }

    public static Repository getInstance () {
        if (Repository.instance == null) {
            Repository.instance = new Repository ();
        }
        return Repository.instance;
    }

    public static Repository getInstance(boolean noGuiMode) {
        Repository repo = getInstance();
        repo.setNoGuiMode(noGuiMode);
        return repo;
    }

    public SerialHelper getSerialComm() {
        return serialComm;
    }

    public byte[] getBytesToRead() {
        return bytesToRead;
    }

    public void setNewValue(DataField newValue) {
        this.newValue = newValue;
    }

    public StringProperty txtRawProperty() {
        return txtRaw;
    }

    public void setTxtRaw(String txtRaw) {
        this.txtRaw.set(txtRaw);
    }

    public StringProperty txtReadProperty() {
        return txtRead;
    }

    public StringProperty txtCrcCalcProperty() {
        return txtCrcCalc;
    }

    public void setTxtCrcCalc(String txtCrcCalc) {
        this.txtCrcCalc.set(txtCrcCalc);
    }

    public StringProperty txtCrcProperty() {
        return txtCrc;
    }

    public void setTxtCrc(String txtCrc) {
        this.txtCrc.set(txtCrc);
    }

    public boolean isLoggerEnabled() {
        return loggerEnabled;
    }

    public void setLoggerEnabled(boolean loggerEnabled) {
        this.loggerEnabled = loggerEnabled;
    }

    public boolean isNoGuiMode() {
        return noGuiMode;
    }

    public void setNoGuiMode(boolean noGuiMode) {
        this.noGuiMode = noGuiMode;
    }

    public boolean hasParsedMaxBytesToRead() {
        return parsedMaxBytesToRead;
    }

    /**
     * Reads an address list CSV file.
     *
     * @param addressList File with address list
     * @return false on error
     */
    public boolean setAddressList(File addressList) {
        if (addressList.exists() && addressList.canRead()) {
            try {
                // All checks passed, only failure might be an Exception now
                readFromCsv(addressList);
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Gets the file stream (skips 1st line) and calls addDataFieldToList() for each line.
     * @param file CSV file
     * @throws IOException on file error
     */
    private void readFromCsv(File file) throws IOException {
        this.fields.clear();
        this.parsedMaxBytesToRead = false;
        Stream<String> fileStream = Files.lines(file.toPath());

        txtRead.set("");
        fileStream
                .skip(1)
                .forEach(this::addDataFieldToList);
    }

    /**
     * Adds the data fields from the CSV file to the local fields variable.
     * @param line One line in the CSV file
     */
    private void addDataFieldToList(String line) {
        String[] splitLine = line.split(CSV_SEPARATOR);
        DataField currentField = null;

        if(!parsedMaxBytesToRead) {
            // First line contains the (max) bytes to read
            bytesToRead = Tools.hexStringToByteArray(splitLine[DataFieldOffset.LENGTH]);
            WtkLogger.getInstance().logGui("Bytes to read: " + Tools.getByteArrayAsHexString(bytesToRead, true));
            parsedMaxBytesToRead = true;
        } else {
            // Create data field from line
            try {
                currentField = new DataField(
                        splitLine[DataFieldOffset.NAME].trim(),
                        splitLine[DataFieldOffset.MENU].trim(),
                        Tools.hexStringToInt(splitLine[DataFieldOffset.ADDRESS].trim()),
                        Tools.hexStringToInt(splitLine[DataFieldOffset.LENGTH].trim()),
                        DataFieldType.fromString(splitLine[DataFieldOffset.TYPE].trim()),
                        Float.parseFloat(splitLine[DataFieldOffset.MIN].trim()),
                        Float.parseFloat(splitLine[DataFieldOffset.MAX].trim()),
                        Integer.parseInt(splitLine[DataFieldOffset.READONLY].trim()) != 0
                );
            } catch (NumberFormatException numEx) {
                WtkLogger.getInstance().errorGui("ERR: CSV-Adressdaten ungültig! Fehler: " + numEx.getMessage());
                return;
            }

            String fieldInfo = currentField.toString();
            fieldInfo = txtRead.get() + fieldInfo;
            WtkLogger.getInstance().logGui("Add field from CSV: " + fieldInfo);
            txtRead.set(fieldInfo + "\n");

            // Add to List
            fields.add(currentField);
        }
    }

    /**
     * Parses every field in our List.
     *
     * @param responseBytes Bytes to parse
     */
    public void parseResponse(byte[] responseBytes) {
        WtkLogger.getInstance().logGui("Parsing response...");
        int bytesToReadInt = new BigInteger(bytesToRead).intValue();

        // Offset for byte stuffing
        int offset = 0;
        txtRead.set("");

        // Go through all elements
        for (DataField field : fields) {
            byte[] byteValue = new byte[field.getLength()];

            for(int i = 0; i < field.length; i++) {
                if(field.getAddress() < bytesToReadInt - 2) {
                    // Check for byte stuffing
                    // 0x10 0x10 -> skip one 0x10
                    // See https://web.cs.wpi.edu/~rek/Undergrad_Nets/C04/BitByteStuffing.pdf, page 8
                    int dleFirst = responseBytes[field.getAddress() + i];
                    int dleSecond = responseBytes[field.getAddress() + i + 1];
                    if(dleFirst == 0x10 && dleSecond == 0x10) {
                        offset++;
                        WtkLogger.getInstance().logGui("Skipped 0x10 - offset set to " + offset + "!");
                    }
                } else {
                    WtkLogger.getInstance().logGui("Byte stuff check skipped (end)");
                }

                byteValue[i] = responseBytes[field.getAddress() + i + offset];
            }

            // Create objects depending on type value
            int fieldIndex = fields.indexOf(field);
            DataField newField = null;
            switch(field.getType()) {
                case DATE:
                    newField = new DateDataField(field);
                    ((DateDataField) newField).setBytes(byteValue);
                    fields.set(fieldIndex, newField);
                    break;
                case TIME:
                    newField = new TimeDataField(field);
                    ((TimeDataField) newField).setBytes(byteValue);
                    fields.set(fieldIndex, newField);
                    break;
                case FLOAT:
                    newField = new FloatDataField(field);
                    ((FloatDataField) newField).setBytes(byteValue);
                    fields.set(fieldIndex, newField);
                    break;
                case BINARY:
                    newField = new BinaryDataField(field);
                    ((BinaryDataField) newField).setBytes(byteValue);
                    fields.set(fieldIndex, newField);
                    break;
                case UNSIGNED_CHAR:
                    if(field.getMin() == 0 && field.getMax() == 1) {
                        // BOOLEAN
                        newField = new BooleanDataField(field);
                        ((BooleanDataField) newField).setBytes(byteValue);
                    } else {
                        newField = new CharDataField(field);
                        ((CharDataField) newField).setBytes(byteValue);
                    }
                    fields.set(fieldIndex, newField);
                    break;
                case UNSIGNED_SHORT:
                    newField = new ShortDataField(field);
                    ((ShortDataField) newField).setBytes(byteValue);
                    fields.set(fieldIndex, newField);
                    break;
            }

            String parseInfo = "";
            parseInfo = String.format("%-25s= %s", field.getName(), newField.toString() + " (" + Tools.getByteArrayAsHexString(newField.getBytes(), true) + ")");
            parseInfo = "\n" + parseInfo;
            String finalParseInfo = parseInfo;
            // Add line to "Read" TextArea
            if(!this.isNoGuiMode()) javafx.application.Platform.runLater(() -> txtRead.set(txtRead.get() + finalParseInfo));

            // Log as normal or no-GUI format
            WtkLogger.getInstance().logGui(parseInfo);
            if(this.isNoGuiMode()) WtkLogger.getInstance().log(String.format("%s=%s", field.getName(), newField));
        }

        if (this.loggerEnabled) {
            this.writeToCsv("wtklogger-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss")) + ".csv");
        }

        if(this.isNoGuiMode()) {
            System.exit(0);
        }
    }

    public ObservableList<DataField> getFields() {
        return this.fields;
    }

    /**
     * Initiates an export for the current fields.
     *
     * @param fileName Save path
     * @return true = success, false = error
     */
    public boolean writeToCsv(String fileName) {
        try {
            WtkLogger.getInstance().logGui("Exporting to " + fileName + "...");
            PrintWriter writer = new PrintWriter(fileName);
            writer.print(this.getCsvList());
            writer.close();
        } catch (IOException ex) {
            WtkLogger.getInstance().errorGui("ERR: " + ex.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Gets the current fields as a CSV string.
     *
     * @return CSV string
     */
    public String getCsvList() {
        StringBuilder exportString = new StringBuilder();
        this.getFields().forEach(field -> {
            exportString.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append(CSV_SEPARATOR);
            exportString.append(field.getName()).append(CSV_SEPARATOR);
            exportString.append(field.toString()).append("\n");
        });

        return exportString.toString();
    }
}
