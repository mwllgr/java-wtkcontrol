package at.mwllgr.wtkcontrol.controller;

import at.mwllgr.wtkcontrol.globals.CommandMode;
import at.mwllgr.wtkcontrol.model.DataField;
import at.mwllgr.wtkcontrol.model.Repository;
import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigInteger;
import java.util.Optional;

public class MainController {
    @FXML
    private ComboBox<String> cmbPorts;
    @FXML
    private TableView<DataField> tvData;
    @FXML
    private TableColumn<DataField, String> colName, colValue;
    ObservableList<DataField> items;

    @FXML
    private Button btnSync, btnRead, btnWakeup, btnClearBuffer, btnOpenClosePort, btnSettings, btnAddressList, btnImport, btnExport;
    final FileChooser fileChooser = new FileChooser();

    private final Repository repository = Repository.getInstance();

    @FXML
    public void initialize() {
        setCmbPorts();

       tvData.setRowFactory(tableView -> {
            TableRow<DataField> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    DataField rowData = row.getItem();
                    this.editField(rowData);
                }
            });
            return row;
        });

        colName.setCellValueFactory(new PropertyValueFactory<DataField, String>("name"));
        colValue.setCellValueFactory(new PropertyValueFactory<DataField, String>("toString"));
    }

    /**
     * Fills the ComboBox with the available serial ports
     * and sets it to the first one if it's only one.
     */
    public void setCmbPorts() {
        SerialPort[] ports = repository.getSerialComm().getSerialPorts();
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
        repository.getSerialComm().setCommPort(cmbPorts.getValue());

        if(repository.getSerialComm().getPortState()) {
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

    /**
     * Manually sends the wake-up command to the heating controller.
     * Needed when the controller suddenly doesn't respond anymore.
     * @param event Button event
     */
    @FXML
    private void sendWakeupCmd(ActionEvent event) {
        repository.getSerialComm().sendWakeupCmd();
    }

    /**
     * Opens a file chooser and allows the user to select a CSV file.
     * The Repository parses the file after that.
     * @param event Button event
     */
    @FXML
    private void selectAddressList(ActionEvent event) {
        fileChooser.setTitle("Adressliste auswählen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(".csv-Dateien", "*.csv"),
                new FileChooser.ExtensionFilter(".txt-Dateien", "*.txt"),
                new FileChooser.ExtensionFilter("Alle Dateien", "*.*")
        );
        File list = fileChooser.showOpenDialog(((Node)event.getTarget()).getScene().getWindow());

        boolean success = false;
        if(list != null) success = repository.setAddressList(list);

        if(!success && list != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dateifehler");

            alert.setHeaderText(null);
            alert.setContentText("Die ausgewählte Adressdatei konnte nicht gelesen werden.");

            alert.showAndWait();
        }
        else
        {
            items = repository.getFields();
            tvData.setItems(items);
        }
    }

    /**
     * Manually requests the whole data as listed in the CSV file.
     * @param event Button event
     */
    @FXML
    private void fullRead(ActionEvent event) {
        repository.getSerialComm().sendCommand(CommandMode.READ_MEMORY, SerialController.FULLREAD_START_ADDR, repository.getBytesToRead());
    }

    private void editField(DataField field) {
        if(field.isReadOnly()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Feld nur lesbar");

            alert.setHeaderText(null);
            alert.setContentText("Das ausgewählte Feld kann nicht bearbeitet werden.");

            alert.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(field.toString());
        dialog.setTitle("Wert bearbeiten");
        dialog.setHeaderText("Wert bearbeiten");
        dialog.setContentText(field.getName());

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) {
            String valueCopy = field.toString();
            this.repository.setValueCopy(valueCopy);
            if(!field.setValueFromString(result.get())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validierungsfehler");

                alert.setHeaderText(null);
                alert.setContentText("Die eingegebenen Daten sind für dieses Feld ungültig.");

                alert.showAndWait();
            }
            else
            {
                byte[] writeAddr = BigInteger.valueOf(field.getAddress()).toByteArray();
                repository.getSerialComm().sendCommand(CommandMode.WRITE_MEMORY, writeAddr, field.getBytes());
            }
        }
    }

    /**
     * Manually requests the whole data as listed in the CSV file.
     * @param event Button event
     */
    @FXML
    private void clearBuffer(ActionEvent event) {
        repository.getSerialComm().clearBuffer();
    }
}
