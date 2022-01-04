package at.mwllgr.wtkcontrol;

import at.mwllgr.wtkcontrol.globals.CommandMode;
import at.mwllgr.wtkcontrol.globals.ExitCode;
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

        if(argList.contains("-h") || argList.contains("--help")) {
            showHelpAndExit();
        }

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
            System.exit(ExitCode.FILE_ERROR);
        }
    }

    private void setupSerialPort() {
        if(argList.contains("--port") && argList.size() > argList.indexOf("--port") + 1) {
            String port = argList.get(argList.indexOf("--port") + 1);
            this.repo.getSerialComm().setCommPort(port);
            this.repo.getSerialComm().sendWakeupCmd();
        } else {
            WtkLogger.getInstance().error("Serial device (parameter --port) not specified!");
            System.exit(ExitCode.MISSING_PARAMETER);
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
            System.exit(ExitCode.FILE_ERROR);
        }
    }

    private void showHelpAndExit() {
        System.out.println("--- wtkcontrol: Help ---");
        System.out.println();
        System.out.println("Attention:" + System.lineSeparator() + "All parameters only work in no-GUI mode!");
        System.out.println();
        System.out.println("Parameters:");
        System.out.printf("  %-20s%s%n", "--no-gui", "Starts the application in CLI mode");
        System.out.printf("  %-20s%s%n", "--port <device>", "[Required] Selects the serial port device");
        System.out.printf("  %-20s%s%n", "--address-file", "Specifies the comma-separated address list, defaults to address-list.csv");
        System.out.printf("  %-20s%s%n", "--logger", "Only used in read mode: Saves the received values into wtk logger-dd-MM-yyyy_hh-mm-ss.csv before the application exits");
        System.out.printf("  %-20s%s%n", "--help, -h", "Shows this help text");
        System.out.println();
        System.out.println("Exit codes:");
        System.out.printf("%4s  %s%n", "1", "General error");
        System.out.printf("%4s  %s%n", "10", "Missing parameter or parameter value");
        System.out.printf("%4s  %s%n", "11", "Serial port (communication) error");
        System.out.printf("%4s  %s%n", "12", "File error, e.g. address file not found");
        System.out.printf("%4s  %s%n", "13", "Field for read/write operation not found in address file");
        System.out.printf("%4s  %s%n", "14", "Field for write operation is read-only");
        System.out.printf("%4s  %s%n", "15", "Validation error: Value has invalid format or is not in a valid range");
        System.exit(0);
    }
}
