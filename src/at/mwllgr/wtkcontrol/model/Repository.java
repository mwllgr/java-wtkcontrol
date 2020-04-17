package at.mwllgr.wtkcontrol.model;

import at.mwllgr.wtkcontrol.controller.SerialController;
import at.mwllgr.wtkcontrol.controller.Tools;
import at.mwllgr.wtkcontrol.globals.DataFieldOffset;
import at.mwllgr.wtkcontrol.globals.DataFieldType;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

public class Repository {
    static final String CSV_SEPARATOR = ",";
    private final SerialController serialComm = SerialController.getInstance();

    // Singleton
    private static Repository instance;

    private Repository() { }
    public static Repository getInstance () {
        if (Repository.instance == null) {
            Repository.instance = new Repository ();
        }
        return Repository.instance;
    }

    HashMap<String, DataField> fields;
    private File addressList;
    int dataFieldCounter = 0;
    byte[] bytesToRead;

    public File getAddressList() {
        return addressList;
    }

    public SerialController getSerialComm() {
        return serialComm;
    }

    public byte[] getBytesToRead() {
        return bytesToRead;
    }

    /**
     * Initializes the HashMap and tries to read the CSV file.
     * @param addressList File with address list
     * @return false on error
     */
    public boolean setAddressList(File addressList) {
        if(fields == null) {
            fields = new LinkedHashMap<>();
        }
        if(addressList.exists() && addressList.canRead()) {
            this.addressList = addressList;
            try {
                readFromCsv(addressList);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Gets the file stream (skips 1st line) and calls addDataFieldToHashMap() for each line.
     * @param file CSV file
     * @throws IOException on file error
     */
    private void readFromCsv(File file) throws IOException {
        Stream<String> fileStream = Files.lines(file.toPath());

        fileStream
                .skip(1)
                .forEach(this::addDataFieldToHashMap);
    }

    /**
     * Adds the data fields from the CSV file to the local fields variable.
     * @param line One line in the CSV file
     */
    private void addDataFieldToHashMap(String line) {
        String[] splitLine = line.split(CSV_SEPARATOR);

        if(dataFieldCounter == 0) {
            // First line contains the (max) bytes to read
            bytesToRead = Tools.hexStringToByteArray(splitLine[DataFieldOffset.LENGTH]);
            System.out.println("Bytes to read: " + Tools.getByteArrayAsHexString(bytesToRead, true));
        }
        else
        {
            // Create data field from line
            DataField currentField = new DataField(
                    splitLine[DataFieldOffset.NAME].trim(),
                    splitLine[DataFieldOffset.MENU].trim(),
                    Tools.hexStringToInt(splitLine[DataFieldOffset.ADDRESS].trim()),
                    Tools.hexStringToInt(splitLine[DataFieldOffset.LENGTH].trim()),
                    DataFieldType.fromString(splitLine[DataFieldOffset.TYPE].trim()),
                    Double.parseDouble(splitLine[DataFieldOffset.MIN].trim()),
                    Double.parseDouble(splitLine[DataFieldOffset.MAX].trim()),
                    Integer.parseInt(splitLine[DataFieldOffset.READONLY].trim()) != 0
            );

            System.out.println("Add field from CSV: " + currentField.toString());
            // Add to HashMap
            fields.put(currentField.getName(), currentField);
        }

        dataFieldCounter++;
    }

    public void parseResponse(byte[] responseBytes) {
        int offset = 0;

        for (String key : fields.keySet()) {
            DataField field = fields.get(key);

            byte[] byteValue = new byte[field.getLength()];
            int bytesToReadInt = new BigInteger(bytesToRead).intValue();

            for(int i = 0; i < field.length; i++) {
                if(field.getAddress() < bytesToReadInt - 2) {
                    int dleFirst = responseBytes[field.getAddress() + i];
                    int dleSecond = responseBytes[field.getAddress() + i + 1];
                    if(dleFirst == 0x10 && dleSecond == 0x10) {
                        offset++;
                    }
                }

                byteValue[i] = responseBytes[field.getAddress() + i + offset];
            }

            System.out.println(key + "-V\t\t" + Tools.getByteArrayAsHexString(byteValue, true));
        }
    }
}
