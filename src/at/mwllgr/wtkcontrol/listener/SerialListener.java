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
    SerialPort port;
    String hexBuffer;

    public SerialListener(SerialPort port) {
        this.port = port;
        System.out.println("SerialListener: Started!");
    }

    @Override
    public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
    @Override
    public void serialEvent(SerialPortEvent event)
    {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;
        byte[] newData = new byte[port.bytesAvailable()];

        int numRead = port.readBytes(newData, newData.length);
        hexBuffer += Tools.getByteArrayAsHexString(newData, false);

        System.out.print(Tools.getByteArrayAsHexString(newData, false));

        Pattern pattern = Pattern.compile("161002(.*?)1003(.{4})16");
        Matcher matcher = pattern.matcher(hexBuffer);

        if(matcher.find())
        {
            System.out.println("Full frame received!");
            System.out.println("CRC Data: " + matcher.group(1));
            handleResponse(matcher.group(1));
            hexBuffer = "";
        }
    }

    public void handleResponse(String response) {
        byte[] responseBytes = Tools.hexStringToByteArray(response);
        if(responseBytes[1] == ResponseMode.READ_RESPONSE) {
            System.out.println("Received response: READ_RESPONSE");
            Repository.getInstance().parseResponse(Arrays.copyOfRange(responseBytes, 2, responseBytes.length));
        }
        else if(responseBytes[1] == ResponseMode.WRITE_RESPONSE)
        {
            System.out.println("Received response: WRITE_RESPONSE");
        }
    }
}
