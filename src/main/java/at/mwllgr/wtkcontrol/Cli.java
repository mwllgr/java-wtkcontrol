package at.mwllgr.wtkcontrol;

import at.mwllgr.wtkcontrol.globals.CommandMode;
import at.mwllgr.wtkcontrol.globals.DataFieldType;
import at.mwllgr.wtkcontrol.globals.ExitCode;
import at.mwllgr.wtkcontrol.helpers.SerialHelper;
import at.mwllgr.wtkcontrol.helpers.WtkLogger;
import at.mwllgr.wtkcontrol.model.DataField;
import at.mwllgr.wtkcontrol.model.Repository;
import at.mwllgr.wtkcontrol.model.types.*;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

        if(argList.contains("--write")) {
            setField();
        } else {
            this.repo.getSerialComm().sendWakeupCmd();
            readAll();
        }
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

    private void setField() {
        if(this.repo.hasParsedMaxBytesToRead()) {
            if(argList.contains("--value") && argList.size() > argList.indexOf("--value") + 1 && argList.size() > argList.indexOf("--write") + 1) {
                String name = argList.get(argList.indexOf("--write") + 1);
                String value = argList.get(argList.indexOf("--value") + 1);

                DataField field = findField(name);
                setFieldOrShowError(field, value);
            } else {
                WtkLogger.getInstance().error("--value <data> or --write <field> not specified!");
                System.exit(ExitCode.MISSING_PARAMETER);
            }
        } else {
            WtkLogger.getInstance().error("Address file not selected/parsed yet!");
            System.exit(ExitCode.FILE_ERROR);
        }
    }

    private DataField findField(String name) {
        Optional<DataField> searchField = this.repo.getFields().stream()
                .filter(f -> f.getName().equals(name))
                .findFirst();

        if(searchField.isEmpty()) {
            WtkLogger.getInstance().error("Field for write operation not found in address list: " + name);
            System.exit(ExitCode.FIELD_NOT_FOUND);
        }

        DataField field = searchField.get();
        if(field.isReadOnly()) {
            WtkLogger.getInstance().error("Field for write operation marked as read-only: " + name);
            System.exit(ExitCode.FIELD_READ_ONLY);
        }

        return field;
    }

    private void setFieldOrShowError(DataField field, String value) {
        String message = "Invalid value specified for field " + field.getName();
        int origMsgLength = message.length();
        DataFieldType type = field.getType();

        DataField typedField = null;
        switch(type) {
            case DATE:
                typedField = new DateDataField(field);
                if(!typedField.setValueFromString(value)) message += " - Format: DD.MM.YY";
                break;
            case TIME:
                typedField = new TimeDataField(field);
                if(!typedField.setValueFromString(value)) message += " - Format: HH:MM:SS - HH can be between 0 and 24!";
                break;
            case FLOAT:
                typedField = new FloatDataField(field);
                if(!typedField.setValueFromString(value)) message += String.format(" - Valid range: %.2f to %.2f", field.getMin(), field.getMax());
                break;
            case BINARY:
                typedField = new BinaryDataField(field);
                if(!typedField.setValueFromString(value)) message += " - Format: Binary with all 8 digits, e.g. 00100100";
                break;
            case UNSIGNED_CHAR:
            case UNSIGNED_SHORT:
                typedField = type == DataFieldType.UNSIGNED_CHAR ? new CharDataField(field) : new ShortDataField(field);
                if(!typedField.setValueFromString(value)) {
                    if (typedField.getMin() == 0 && typedField.getMax() == 1) {
                        message += " - Boolean field: Only 0 and 1 allowed!";
                    } else {
                        message += String.format(" - Valid range: %.0f to %.0f", typedField.getMin(), typedField.getMax());
                    }
                }
        }

        if(message.length() != origMsgLength) {
            WtkLogger.getInstance().error(message);
            System.exit(ExitCode.VALIDATION_ERROR);
            return;
        }

        this.repo.getSerialComm().sendWakeupCmd();
        byte[] writeAddr = BigInteger.valueOf(typedField.getAddress()).toByteArray();
        this.repo.getSerialComm().sendCommand(CommandMode.WRITE_MEMORY, writeAddr, typedField.getBytes());
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
        System.out.printf("  %-20s%s%n", "--write <name>", "Requires --value, writes <data> to field named <name>");
        System.out.printf("  %-20s%s%n", "--value <data>", "Requires --write, <data> has to be in a valid format/range");
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
        System.out.printf("%4s  %s%n", "16", "Address list malformed (e.g. invalid data type)");
        System.exit(0);
    }
}
