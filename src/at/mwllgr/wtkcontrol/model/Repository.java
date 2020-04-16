package at.mwllgr.wtkcontrol.model;

import at.mwllgr.wtkcontrol.controller.SerialController;
import at.mwllgr.wtkcontrol.controller.Tools;
import at.mwllgr.wtkcontrol.globals.DataFieldOffsets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.stream.Stream;

public class Repository {
    static final String CSV_SEPARATOR = ",";
    private final SerialController serialComm = new SerialController();

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

    public boolean setAddressList(File addressList) {
        if(fields == null) {
            fields = new HashMap<>();
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

    private void readFromCsv(File file) throws IOException {
        Stream<String> fileStream = Files.lines(file.toPath());

        fileStream
                .skip(1)
                .forEach(this::addDataFieldToHashMap);
    }

    private void addDataFieldToHashMap(String line) {
        String[] splitLine = line.split(CSV_SEPARATOR);

        if(dataFieldCounter == 0) {
            bytesToRead = Tools.hexStringToByteArray(splitLine[DataFieldOffsets.LENGTH]);
            System.out.println("Bytes to read: " + Tools.getByteArrayAsHexString(bytesToRead));
        }

        dataFieldCounter++;
    }
}
