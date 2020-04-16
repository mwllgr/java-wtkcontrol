package at.mwllgr.wtkcontrol.controller;

import at.mwllgr.wtkcontrol.globals.CommandMode;
import at.mwllgr.wtkcontrol.listener.SerialListener;
import com.fazecast.jSerialComm.*;
import javafx.scene.control.Alert;

public class SerialController {
    SerialPort port; // Currently used serial port
    SerialListener listener;

    /**
     * Gets an array of available serial ports.
     * @return Available serial ports
     */
    public SerialPort[] getSerialPorts() {
        return SerialPort.getCommPorts();
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
        System.out.println(Tools.getByteArrayAsHexString(buffer));
        System.out.println();

        return result;
    }
}
