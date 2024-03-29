package at.mwllgr.wtkcontrol.dialogs;

import at.mwllgr.wtkcontrol.helpers.WtkLogger;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * Special change dialog for boolean fields.
 */
public class BooleanDialog {
    private final String changeValue;
    private final boolean oldValue;

    public BooleanDialog(String changeValue, boolean oldValue) {
        this.changeValue = changeValue;
        this.oldValue = oldValue;
    }

    public String show() {
        // Create the custom dialog.
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Wert ändern");
        dialog.setHeaderText("Wert ändern");

        // ----- Custom icon -----
        Label img = new Label();
        img.getStyleClass().addAll("alert", "confirmation", "dialog-pane");
        dialog.setGraphic(img);
        // ----- ----------- -----

        // Set the button types.
        ButtonType saveBtnType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        CheckBox newValue = new CheckBox(this.changeValue);
        newValue.setSelected(this.oldValue);
        grid.add(newValue, 0, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                return newValue.isSelected();
            }
            return null;
        });

        Optional<Boolean> result = dialog.showAndWait();

        if(result.isPresent()) {
            WtkLogger.getInstance().logGui(this.changeValue + " = " + result.get().toString());
            // Return correct value for serial communication
            return result.get() ? "1" : "0";
        }

        return "";
    }
}
