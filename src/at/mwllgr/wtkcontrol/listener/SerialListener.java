package at.mwllgr.wtkcontrol.listener;

import at.mwllgr.wtkcontrol.controller.CRC16;
import at.mwllgr.wtkcontrol.controller.SerialController;
import at.mwllgr.wtkcontrol.controller.Tools;
import at.mwllgr.wtkcontrol.globals.CommandMode;
import at.mwllgr.wtkcontrol.globals.ResponseMode;
import at.mwllgr.wtkcontrol.model.DataField;
import at.mwllgr.wtkcontrol.model.Repository;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.scene.control.Alert;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SerialListener implements SerialPortDataListener {
    // Complete frame:
    // FF DLE STX (DATA) DLE ETX (CRC) FF
    static final String COMPLETE_FRAME_REGEX = "161002(.*?)1003(.{4})16";

    SerialPort port;
    String hexBuffer;

    public SerialListener(SerialPort port) {
        this.port = port;
        System.out.println("SerialListener: Started!");
    }

    public void clearBuffer() {
        this.hexBuffer = "";
        System.out.println("Buffer cleared.");
    }

    @Override
    public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }

    /**
     * Gets called when a serial event happened.
     * @param event Event information object
     */
    @Override
    public void serialEvent(SerialPortEvent event)
    {
        // Only if new data is available
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;
        byte[] newData = new byte[port.bytesAvailable()];

        port.readBytes(newData, newData.length);
        hexBuffer += Tools.getByteArrayAsHexString(newData, false);
        System.out.print(Tools.getByteArrayAsHexString(newData, false));

        Pattern pattern = Pattern.compile(COMPLETE_FRAME_REGEX);
        Matcher matcher = pattern.matcher(hexBuffer);

        // Check for a complete frame
        if(matcher.find())
        {
            String crcData = matcher.group(1);
            String recvCrc = matcher.group(2);

            System.out.println("Complete frame received!");
            System.out.println("CRC Data: " + crcData);
            handleCompleteFrame(crcData, recvCrc);
            hexBuffer = "";
        }
    }

    /**
     * Parses the received response and checks the response type.
     * Available response types are available in globals.ResponseMode
     * @param crcData Received frame as hex string
     * @param recvCrc Received CRC as hex string
     */
    public void handleCompleteFrame(String crcData, String recvCrc) {
        byte[] responseBytes = Tools.hexStringToByteArray(crcData);

        System.out.print("Checking CRC... ");
        if(checkCrc(responseBytes, Tools.hexStringToByteArray(recvCrc))) {
            if (responseBytes[1] == ResponseMode.READ_RESPONSE) {
                System.out.println(" -> +CRC: OK");
                System.out.println("Received response: READ_RESPONSE");
                Repository.getInstance().parseResponse(Arrays.copyOfRange(responseBytes, 2, responseBytes.length));
            } else if (responseBytes[1] == ResponseMode.WRITE_RESPONSE) {
                // Write operation ACK
                System.out.println("Received response: WRITE_RESPONSE");
                Repository repo = Repository.getInstance();
                // Read values again
                this.clearBuffer();
                repo.getSerialComm().sendCommand(CommandMode.READ_MEMORY, SerialController.FULLREAD_START_ADDR, repo.getBytesToRead());
            }
        }
        else
        {
            System.err.println(" -> CRC: ERR");
        }
    }

    /**
     * Returns true if the receivedCrc for responseBytes is a match.
     * @param responseBytes Bytes to calculate CRC for
     * @param recvCrcBytes Received CRC to compare to
     * @return true = CRC matches
     */
    private boolean checkCrc(byte[] responseBytes, byte[] recvCrcBytes) {
        byte[] calcCrcBytes = CRC16.calculate(responseBytes);
        System.out.print(
                "CRC-Recv: " + Tools.getByteArrayAsHexString(recvCrcBytes, false)
                + " - CRC-Calc: " + Tools.getByteArrayAsHexString(calcCrcBytes, false)
        );

        return Arrays.equals(calcCrcBytes, recvCrcBytes);
    }
}
