package at.mwllgr.wtkcontrol.helpers;

import at.mwllgr.wtkcontrol.globals.CommandMode;
import at.mwllgr.wtkcontrol.listener.SerialListener;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import javafx.scene.control.Alert;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SerialHelper {
    public static final byte[] FULLREAD_START_ADDR = {0x00, 0x00};
    static final byte[] COMMAND_START = {0x10, 0x02};
    static final byte[] SLAVE_ADDR = {0x01};
    static final byte[] COMMAND_END = {0x10, 0x03};

    // Singleton
    private static SerialHelper instance;

    private SerialHelper() {
    }

    public static SerialHelper getInstance() {
        if (SerialHelper.instance == null) {
            SerialHelper.instance = new SerialHelper();
        }
        return SerialHelper.instance;
    }

    SerialPort port; // Currently used serial port
    SerialListener listener;

    /**
     * Gets an array of available serial ports.
     * @return Available serial ports
     */
    public SerialPort[] getSerialPorts() {
        return SerialPort.getCommPorts();
    }

    public void clearBuffer() {
        this.listener.clearBuffer();
    }

    /***
     * Validates the port (null-check) and tries to open/close it.
     *
     * @param portName Serial port name (COM* on Windows, tty* on Linux)
     * @return Found serial port
     */
    public SerialPort setCommPort(String portName) {
        try {
            System.out.print("Validating port: " + portName + "... ");

            // Only overwrite variable if it's not the same port
            // We have to do this to keep track of the state
            if(this.port == null || !this.port.isOpen())
                this.port = SerialPort.getCommPort(portName);

            System.out.println("OK");
            openOrCloseSerialPort();
        } catch (SerialPortInvalidPortException ex) {
            System.err.println("ERR");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ungültiger Port");

            alert.setHeaderText(null);
            alert.setContentText("Überprüfen Sie den eingegebenen Port.");

            alert.showAndWait();
        }

        return null;
    }

    /**
     * Closes or opens the serial port depending on the current state.
     */
    private void openOrCloseSerialPort() {
        if (this.port.isOpen()) {
            System.out.print("Closing " + this.port.getSystemPortName() + "... ");
            port.closePort();
            System.out.println("OK");
        } else {
            System.out.print("Opening " + this.port.getSystemPortName() + "... ");

            this.port.setBaudRate(9600);
            if (this.port.openPort()) {
                // Serial port opened successfully
                System.out.println("OK");

                // Add listener
                listener = new SerialListener(port);
                port.addDataListener(listener);
            } else {
                System.err.println("ERR");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Portfehler");

                alert.setHeaderText(null);
                alert.setContentText("Der angegebene Port konnte nicht geöffnet werden.");

                alert.showAndWait();
            }
        }
    }

    /**
     * Gets the current state (opened/closed) of the serial port.
     * @return true = opened, false = closed
     */
    public boolean getPortState() {
        return this.port != null && this.port.isOpen();
    }

    /**
     * Returns the current serial port.
     * @return Current serial port
     */
    public SerialPort getPort() {
        return this.port;
    }

    /**
     * Sends "AT" and a carriage return
     */
    public void sendWakeupCmd() {
        System.out.print("Sending wake up command... ");
        this.writeBytesRaw(CommandMode.WAKEUP);
    }

    /**
     * Syncs the heating clock with the local system clock.
     */
    public void syncTimeDate() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String outputString = currentDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss"));
        System.out.println("Setting date to: " + outputString);

        byte[] writeDateTime = new byte[]{
                Integer.valueOf(currentDateTime.getSecond()).byteValue(),
                Integer.valueOf(currentDateTime.getMinute()).byteValue(),
                Integer.valueOf(currentDateTime.getHour()).byteValue(),
                Integer.valueOf(currentDateTime.getDayOfMonth()).byteValue(),
                Integer.valueOf(currentDateTime.getMonthValue()).byteValue(),
                // Get 2-digit year
                Integer.valueOf(Integer.parseInt(currentDateTime.format(DateTimeFormatter.ofPattern("yy")))).byteValue()
        };

        this.sendCommand(CommandMode.WRITE_DATETIME, new byte[]{0x00, 0x00}, writeDateTime);
    }

    /**
     * Sends a command with the specified mode, address and length/bytes.
     *
     * @param mode  Read, write or DateTime
     * @param addr  Start address
     * @param bytes Length or bytes to write
     */
    public void sendCommand(byte[] mode, byte[] addr, byte[] bytes) {
        // Replace DLE with DLE DLE (byte stuffing)
        String rawBytes = Tools.getByteArrayAsHexString(bytes, true);
        rawBytes = rawBytes.replace("10", "1010").replace(" ", "");
        byte[] escapedBytes = Tools.hexStringToByteArray(rawBytes);

        // Prepare bytes for CRC calculation
        byte[] crcCalcBytes = new byte[SLAVE_ADDR.length + mode.length + addr.length + escapedBytes.length];
        ByteBuffer crcBuff = ByteBuffer.wrap(crcCalcBytes);
        crcBuff.put(SLAVE_ADDR);
        crcBuff.put(mode);
        crcBuff.put(addr);
        crcBuff.put(escapedBytes);
        crcCalcBytes = crcBuff.array();

        // Calculate the CRC
        byte[] crc = CRC16.calculate(crcCalcBytes);

        // Prepare array for sending all the needed bytes
        byte[] allBytes = new byte[COMMAND_START.length + crcCalcBytes.length + COMMAND_END.length + crc.length];

        ByteBuffer buff = ByteBuffer.wrap(allBytes);
        buff.put(COMMAND_START);
        buff.put(crcCalcBytes);
        buff.put(COMMAND_END);
        buff.put(crc);

        // Watch the magic happen
        writeBytesRaw(buff.array());
    }

    /**
     * Writes the byte array to the serial port.
     * @param buffer Bytes to write
     * @return -1 on error
     */
    public int writeBytesRaw(byte[] buffer) {
        int result = this.port.writeBytes(buffer, buffer.length);

        if(result != -1) {
            System.out.println("OK");
        }
        else
        {
            System.err.println("ERR");
        }

        System.out.print("Sent bytes: ");
        System.out.println(Tools.getByteArrayAsHexString(buffer, true));
        System.out.println();

        return result;
    }
}
