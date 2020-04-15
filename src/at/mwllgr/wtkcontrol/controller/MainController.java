package at.mwllgr.wtkcontrol.controller;

import com.fazecast.jSerialComm.SerialPort;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class MainController {
    @FXML
    private ComboBox<String> cmbPorts;
    @FXML
    private Button btnSync, btnRead, btnWakeup, btnClearBuffer, btnOpenClosePort, btnSettings, btnAddressList, btnImport, btnExport;
    private final SerialController serialComm;

    public MainController() {
        serialComm = new SerialController();
    }

    @FXML
    public void initialize() {
        setCmbPorts();
    }

    /**
     * Fills the ComboBox with the available serial ports
     * and sets it to the first one if it's only one.
     */
    public void setCmbPorts() {
        SerialPort[] ports = serialComm.getSerialPorts();
        for (SerialPort port : ports) {
            String portName = port.getSystemPortName();
            cmbPorts.getItems().add(portName);
        }

        if(ports.length == 1) {
            cmbPorts.setValue(cmbPorts.getItems().get(0));
        }
    }

    /**
     * Button function for opening/closing the serial port in the ComboBox
     * @param event Button event
     */
    @FXML
    private void openOrClosePort(ActionEvent event) {
        serialComm.setCommPort(cmbPorts.getValue());

        if(serialComm.getPortState()) {
            // Port now opened
            btnOpenClosePort.setText("Port schließen");
            btnWakeup.setDisable(false);
            btnSync.setDisable(false);
        }
        else
        {
            // Port now closed
            btnOpenClosePort.setText("Port öffnen");
            btnWakeup.setDisable(true);
            btnSync.setDisable(true);
        }
    }

    @FXML
    private void sendWakeupCmd(ActionEvent event) {
        serialComm.sendWakeupCmd();
    }
}
