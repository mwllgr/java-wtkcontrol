package at.mwllgr.wtkcontrol.controller;

import at.mwllgr.wtkcontrol.dialogs.BooleanDialog;
import at.mwllgr.wtkcontrol.globals.CommandMode;
import at.mwllgr.wtkcontrol.model.DataField;
import at.mwllgr.wtkcontrol.model.Repository;
import at.mwllgr.wtkcontrol.model.types.BooleanDataField;
import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Main window controller.
 */
public class MainController {
    @FXML
    private ComboBox<String> cmbPorts; // Serial ports combo box
    @FXML
    private TableView<DataField> tvData;
    @FXML
    private TableColumn<DataField, String> colName, colValue;
    ObservableList<DataField> items;

    @FXML
    private TextArea txtRaw, txtRead, txtCrcCalc, txtCrc;

    @FXML
    private Button btnSync, btnRead, btnWakeup, btnClearBuffer, btnOpenClosePort, btnSettings, btnAddressList, btnExport;
    final FileChooser fileChooser = new FileChooser();
    final FileChooser fileSaver = new FileChooser();

    private final Repository repository = Repository.getInstance();

    @FXML
    public void initialize() {
        // Populate combo box with serial ports
        setCmbPorts();

        // Register double-click
        tvData.setRowFactory(tableView -> {
            TableRow<DataField> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    DataField rowData = row.getItem();
                    this.editField(rowData);
                }
            });
            return row;
        });

        // Populate cells with name/value-pairs
        colName.setCellValueFactory(new PropertyValueFactory<DataField, String>("name"));
        colValue.setCellValueFactory(new PropertyValueFactory<DataField, String>("toString"));

        // Bind text areas for showing the hex data
        txtRaw.textProperty().bind(repository.txtRawProperty());
        txtRead.textProperty().bind(repository.txtReadProperty());
        txtCrcCalc.textProperty().bind(repository.txtCrcCalcProperty());
        txtCrc.textProperty().bind(repository.txtCrcProperty());
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

        if (ports.length == 1) {
            // Only one port found, set as default
            cmbPorts.setValue(cmbPorts.getItems().get(0));
        }
    }

    /**
     * Button function for opening/closing the serial port in the ComboBox
     *
     * @param event Button event
     */
    @FXML
    private void openOrClosePort(ActionEvent event) {
        repository.getSerialComm().setCommPort(cmbPorts.getValue());

        if (repository.getSerialComm().getPortState()) {
            // Port now opened
            btnOpenClosePort.setText("Port schließen");
            btnWakeup.setDisable(false);
            btnSync.setDisable(false);
            btnExport.setDisable(false);
        } else {
            // Port now closed
            btnOpenClosePort.setText("Port öffnen");
            btnWakeup.setDisable(true);
            btnSync.setDisable(true);
            btnExport.setDisable(true);
        }
    }

    /**
     * Manually sends the wake-up command to the heating controller.
     * Needed when the controller suddenly doesn't respond anymore.
     *
     * @param event Button event
     */
    @FXML
    private void sendWakeupCmd(ActionEvent event) {
        repository.getSerialComm().sendWakeupCmd();
    }

    /**
     * Opens a file chooser and allows the user to select a CSV file.
     * The Repository parses the file after that.
     *
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
        File list = fileChooser.showOpenDialog(((Node) event.getTarget()).getScene().getWindow());

        boolean success = false;
        if (list != null) success = repository.setAddressList(list);

        // Only if actually opened and no error occurred
        if (!success && list != null) {
            // File error
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dateifehler");

            alert.setHeaderText(null);
            alert.setContentText("Die ausgewählte Adressdatei konnte nicht gelesen werden.");

            alert.showAndWait();
        } else {
            items = repository.getFields();
            tvData.setItems(items);
        }
    }

    /**
     * Manually requests the whole data as listed in the CSV file.
     *
     * @param event Button event
     */
    @FXML
    private void fullRead(ActionEvent event) {
        this.clearBuffer(null);
        repository.getSerialComm().sendCommand(CommandMode.READ_MEMORY, SerialController.FULLREAD_START_ADDR, repository.getBytesToRead());
    }

    /**
     * Called on field double-click in table view.
     *
     * @param field Selected field
     */
    private void editField(DataField field) {
        // Only allow if field is writable
        if (field.isReadOnly()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Feld nur lesbar");

            alert.setHeaderText(null);
            alert.setContentText("Das ausgewählte Feld kann nicht bearbeitet werden.");

            alert.showAndWait();
            return;
        }

        if (field instanceof BooleanDataField) {
            // Show special CheckBox true/false dialog
            BooleanDialog boolDiag = new BooleanDialog(field.getName(), ((BooleanDataField) field).getValue());
            String newVal = boolDiag.show();
            if (!newVal.isEmpty()) {
                // Only if "OK"/"Save" clicked
                field.setValueFromString(newVal);
                this.repository.setNewValue(field);
                byte[] writeAddr = BigInteger.valueOf(field.getAddress()).toByteArray();
                repository.getSerialComm().sendCommand(CommandMode.WRITE_MEMORY, writeAddr, field.getBytes());
            }
        } else {
            // String/Numeric value
            TextInputDialog dialog = new TextInputDialog(field.toString());
            dialog.setTitle("Wert bearbeiten");
            dialog.setHeaderText("Wert bearbeiten");
            dialog.setContentText(field.getName());

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String currentValue = field.toString();
                if (!field.setValueFromString(result.get())) {
                    // Failed validation
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Validierungsfehler");

                    alert.setHeaderText(null);
                    alert.setContentText("Die eingegebenen Daten sind für dieses Feld ungültig.");

                    alert.showAndWait();
                } else {
                    // Write data to heating controller
                    this.repository.setNewValue(field);
                    byte[] writeAddr = BigInteger.valueOf(field.getAddress()).toByteArray();
                    repository.getSerialComm().sendCommand(CommandMode.WRITE_MEMORY, writeAddr, field.getBytes());
                    field.setValueFromString(currentValue);
                }
            }
        }
    }

    /**
     * Manually requests the whole data as listed in the CSV file.
     *
     * @param event Button event
     */
    @FXML
    private void clearBuffer(ActionEvent event) {
        tvData.getSortOrder().clear();
        repository.getSerialComm().clearBuffer();
    }

    /**
     * Opens a file chooser and allows the user to select a CSV file.
     * The Repository parses the file after that.
     *
     * @param event Button event
     */
    @FXML
    private void exportToFile(ActionEvent event) {
        // Configure dialog
        fileSaver.setTitle("Speicherort auswählen");
        fileSaver.setInitialFileName("wtkexport-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss")) + ".csv");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Kommagetrennt (*.csv)", "*.txt");
        fileSaver.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(".csv-Dateien", "*.csv")
        );
        // Show dialog
        File csv = fileSaver.showSaveDialog(((Node) event.getTarget()).getScene().getWindow());

        // Write to CSV
        boolean success = false;
        if (csv != null) success = repository.writeToCsv(csv.getPath());

        if (!success && csv != null) {
            // File error
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dateifehler");

            alert.setHeaderText(null);
            alert.setContentText("Die Daten konnten nicht exportiert werden.");

            alert.showAndWait();
        } else if (success) {
            // Export successful
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export abgeschlossen");

            alert.setHeaderText(null);
            alert.setContentText("Die Daten wurden erfolgreich exportiert.");

            alert.showAndWait();
        }
    }

    /**
     * Called when "Sync time/date" button clicked.
     *
     * @param event Button event
     */
    @FXML
    private void syncDateTime(ActionEvent event) {
        this.clearBuffer(null);
        repository.getSerialComm().syncTimeDate();
    }
}
