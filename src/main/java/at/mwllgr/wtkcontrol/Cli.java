package at.mwllgr.wtkcontrol;

import at.mwllgr.wtkcontrol.globals.CommandMode;
import at.mwllgr.wtkcontrol.helpers.SerialHelper;
import at.mwllgr.wtkcontrol.helpers.WtkLogger;
import at.mwllgr.wtkcontrol.model.Repository;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Cli {
    private static Cli instance;
    private final List<String> argList;
    Repository repo = Repository.getInstance(true);

    public static void getInstance(String[] args) {
        if (Cli.instance == null) {
            Cli.instance = new Cli(args);
        }
    }

    private Cli(String[] args) {
        argList = Arrays.asList(args);
        setAddressList();
        setupSerialPort();
        setupLogger();
        readAll();
    }

    private void setAddressList() {
        String addressList = "address-list.csv";

        if(argList.contains("--address-list") && argList.size() > argList.indexOf("--address-list") + 1) {
            addressList = argList.get(argList.indexOf("--address-list") + 1);
        }

        if(!repo.setAddressList(new File(addressList))) {
            WtkLogger.getInstance().error("Cannot read address list: " + addressList);
            System.exit(255);
        }
    }

    private void setupSerialPort() {
        if(argList.contains("--port") && argList.size() > argList.indexOf("--port") + 1) {
            String port = argList.get(argList.indexOf("--port") + 1);
            this.repo.getSerialComm().setCommPort(port);
            this.repo.getSerialComm().sendWakeupCmd();
        } else {
            WtkLogger.getInstance().error("Serial device (parameter --port) not specified!");
            System.exit(255);
        }
    }

    private void setupLogger() {
        if(argList.contains("--logger")) {
            this.repo.setLoggerEnabled(true);
        }
    }

    private void readAll() {
        if(this.repo.hasParsedMaxBytesToRead()) {
            this.repo.getSerialComm().sendCommand(
                    CommandMode.READ_MEMORY,
                    SerialHelper.FULLREAD_START_ADDR,
                    this.repo.getBytesToRead()
            );
        } else {
            WtkLogger.getInstance().error("Address file not selected/parsed yet!");
            System.exit(255);
        }
    }
}
