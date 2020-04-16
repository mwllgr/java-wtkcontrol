package at.mwllgr.wtkcontrol.listener;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class SerialListener implements SerialPortDataListener {
    SerialPort port;

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
        System.out.println("SerialListener: Read " + numRead + " bytes.");
    }
}
