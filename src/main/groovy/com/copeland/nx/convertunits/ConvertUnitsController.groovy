package com.copeland.nx.convertunits

import javafx.fxml.FXML
import javafx.scene.control.Label

class ConvertUnitsController {
    @FXML
    private Label status

    @FXML
    protected void convertBtnAction() {
        status.setText("convertBtn clicked!")

    }
}