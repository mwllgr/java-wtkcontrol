package at.mwllgr.wtkcontrol.listener;

import at.mwllgr.wtkcontrol.controller.Tools;
import at.mwllgr.wtkcontrol.globals.ResponseMode;
import at.mwllgr.wtkcontrol.model.Repository;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

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
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return;
        byte[] newData = new byte[port.bytesAvailable()];

        hexBuffer += Tools.getByteArrayAsHexString(newData, false);

        System.out.print(Tools.getByteArrayAsHexString(newData, false));

        Pattern pattern = Pattern.compile(COMPLETE_FRAME_REGEX);
        Matcher matcher = pattern.matcher(hexBuffer);

        // Check for a complete frame
        if(matcher.find())
        {
            System.out.println("Complete frame received!");
            // Get the data for CRC calculation
            System.out.println("CRC Data: " + matcher.group(1));
            handleResponse(matcher.group(1));
            hexBuffer = "";
        }
    }

    /**
     * Parses the received response and checks the response type.
     * Available response types are available in globals.ResponseMode
     * @param response Received frame as hex string
     */
    public void handleResponse(String response) {
        byte[] responseBytes = Tools.hexStringToByteArray(response);
        if(responseBytes[1] == ResponseMode.READ_RESPONSE) {
            System.out.println("Received response: READ_RESPONSE");
            Repository.getInstance().parseResponse(Arrays.copyOfRange(responseBytes, 2, responseBytes.length));
        }
        else if(responseBytes[1] == ResponseMode.WRITE_RESPONSE)
        {
            // Write operation ACK
            System.out.println("Received response: WRITE_RESPONSE");
        }
    }
}
