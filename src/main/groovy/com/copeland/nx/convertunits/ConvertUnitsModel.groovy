package com.copeland.nx.convertunits

import javafx.beans.property.StringProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty

class ConvertUnitsModel {

    // Define variables to store the properties
    private final StringProperty inputFile = new SimpleStringProperty()
    private final StringProperty outputDir = new SimpleStringProperty()
    private final BooleanProperty mmUnits = new SimpleBooleanProperty()
    private final StringProperty status = new SimpleStringProperty()
    private final BooleanProperty inputIsDir = new SimpleBooleanProperty()

    // javafx.scene.Node node // for file chooser

    ArrayList<String> masterList = new ArrayList() // for output stream


    // File inputFile

    // Constructor (optional, for convenience)
    ConvertUnitsModel(String inputFile, String outputDir, Boolean mmUnits, String status) {
        this.inputFile.set(inputFile)
        this.outputDir.set(outputDir)
        this.mmUnits.set(mmUnits)
        this.status.set(status)
        this.inputIsDir.set(false) // default on construction
    }

    ConvertUnitsModel() {
        this("", "", false, "Select a file or directory to convert...")
    }

    // Define getters and setters for the values (standard JavaBeans pattern)
    final String getInputFile() {
        return inputFile.get()
    }

    final void setInputFile(String value) {
        inputFile.set(value)
    }

    final String getOutputDir() {
        return outputDir.get()
    }

    final void setOutputDir(String value) {
        outputDir.set(value)
    }

    final Boolean getMmUnits() {
        return mmUnits.get()
    }

    final void setMmUnits(Boolean value) {
        mmUnits.set(value)
    }

    final String getStatus() {
        return status.get()
    }

    final void setStatus(String value) {
        status.set(value)
    }

    final Boolean getInputIsDir() {
        return inputIsDir.get()
    }

    final void setInputIsDir(Boolean value) {
        inputIsDir.set(value)
    }

    // Define getters for the properties themselves (JavaFX pattern)
    StringProperty inputFileProperty() {
        return inputFile
    }

    StringProperty outputDirProperty() {
        return outputDir
    }

    BooleanProperty mmUnitsProperty() {
        return mmUnits
    }

    StringProperty statusProperty() {
        return status
    }

    BooleanProperty inputIsDirProperty() {
        return inputIsDir
    }


}
