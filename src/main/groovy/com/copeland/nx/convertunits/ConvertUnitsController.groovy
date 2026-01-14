package com.copeland.nx.convertunits

import groovy.transform.CompileStatic
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.control.Toggle
import javafx.scene.control.ToggleGroup
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Window
import javafx.stage.FileChooser.ExtensionFilter
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority

import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager


@CompileStatic
class ConvertUnitsController {

    private static final Logger logger = LogManager.getLogger()



    @FXML
    private Label statusLabel

    @FXML
    private TextField inputField

    @FXML
    private TextField outputField

    @FXML
    private RadioButton fileRadioButton

    @FXML
    private RadioButton dirRadioButton

    @FXML
    private RadioButton mmRadioButton

    @FXML
    private RadioButton inchRadioButton

    @FXML
    ToggleGroup inputToggleGroup

    @FXML
    ToggleGroup unitsToggleGroup

    ConvertUnitsModel model

    ConvertUnitsService service

    ResourceBundle bundle


    void initialize() {
        // Initialize the model
        logger.info("Initializing the controller...")
        // bundle = resources
        model = new ConvertUnitsModel("", "", false, "Select a file or directory to convert...")
        service = new ConvertUnitsService()


        // Bind the UI controls to the model properties

        // Bidirectional binding for editable fields (TextField <-> Model)
        // firstNameField.textProperty().bindBidirectional(personModel.firstNameProperty());
        inputField.textProperty().bindBidirectional(model.inputFileProperty())
        outputField.textProperty().bindBidirectional(model.outputDirProperty())

        // Unidirectional binding for display-only fields (Model -> Label)
        // fullNameLabel.textProperty().bind(personModel.firstNameProperty().concat(" ").concat(personModel.lastNameProperty()));
        statusLabel.textProperty().bind(model.statusProperty())


        // Assign RadioButtons to the ToggleGroup (already assigned in FXML)
        // dirRadioButton.setToggleGroup(inputToggleGroup)
        // fileRadioButton.setToggleGroup(inputToggleGroup)

        // for inputIsDir
        // Assign user data to help with value mapping
        fileRadioButton.setUserData(Boolean.FALSE) // set false button
        dirRadioButton.setUserData(Boolean.TRUE) // set true button

        // --- Bidirectional synchronization logic ---

        // 1. Update model when ToggleGroup selection changes
        inputToggleGroup.selectedToggleProperty().addListener({ observable, oldValue, newValue ->
            if (newValue) {
                // Get the Boolean value from the selected toggle's UserData
                Toggle toggle = newValue as Toggle
                Boolean value = toggle.getUserData() as Boolean
                model.setInputIsDir(value)
            }
        } as ChangeListener)

        // 2. Update ToggleGroup selection when model property changes
        model.inputIsDirProperty().addListener({ observable, oldValue, newValue ->
            // Find the corresponding toggle button and select it
            Boolean boolValue = newValue as Boolean
            def targetToggle = inputToggleGroup.toggles.find { Toggle toggle -> toggle.getUserData() == boolValue }
            if (targetToggle) {
                inputToggleGroup.selectToggle(targetToggle)
            }
        } as ChangeListener)

        // --- Initial synchronization ---
        // Set initial state from the model
        // Note: Ensure an initial selection is made, e.g., in FXML or code.
        if (model.getInputIsDir()) {
            dirRadioButton.setSelected(true)
        } else {
            fileRadioButton.setSelected(true)
        }



        // Assign RadioButtons to the ToggleGroup (already assigned in FXML)
        // inchRadioButton.setToggleGroup(unitsToggleGroup)
        // mmRadioButton.setToggleGroup(unitsToggleGroup)

        // for mmUnits
        // Assign user data to help with value mapping
        inchRadioButton.setUserData(Boolean.FALSE) // set false button
        mmRadioButton.setUserData(Boolean.TRUE) // set true button

        // --- Bidirectional synchronization logic ---
        // 1. Update model when ToggleGroup selection changes
        unitsToggleGroup.selectedToggleProperty().addListener({ observable, oldValue, newValue ->
            if (newValue) {
                // Get the Boolean value from the selected toggle's UserData
                Toggle toggle = newValue as Toggle
                Boolean value = toggle.getUserData() as Boolean
                model.setMmUnits(value)
            }
        } as ChangeListener)

        // 2. Update ToggleGroup selection when model property changes
        model.mmUnitsProperty().addListener({ observable, oldValue, newValue ->
            // Find the corresponding toggle button and select it
            Boolean boolValue = newValue as Boolean
            def targetToggle = unitsToggleGroup.toggles.find { Toggle toggle -> toggle.getUserData() == boolValue }
            if (targetToggle) {
                unitsToggleGroup.selectToggle(targetToggle)
            }
        } as ChangeListener)

        // --- Initial synchronization ---
        // Set initial state from the model
        // Note: Ensure an initial selection is made, e.g., in FXML or code.
        if (model.getMmUnits()) {
            mmRadioButton.setSelected(true)
        } else {
            inchRadioButton.setSelected(true)
        }


    }

    @FXML
    protected void inputBtnAction() {
        logger.info("Input button clicked")
        model.setStatus("Convert button clicked!")

        Window owner = statusLabel.getScene().getWindow() // any injected UI element will work

        // Optional: Set initial directory to user's home folder
        File initialDir = new File(System.getProperty("user.home"))


        // check if we need a file or directory chooser
        if (model.inputIsDir) {

            DirectoryChooser directoryChooser = new DirectoryChooser()
            directoryChooser.setTitle("Select a Directory to convert...")

            if (initialDir.exists() && initialDir.isDirectory()) {
                directoryChooser.setInitialDirectory(initialDir);
            }

            File selectedDirectory = directoryChooser.showDialog(owner)
            if (selectedDirectory) {
                model.setInputFile(selectedDirectory.getAbsolutePath())
                model.setStatus(bundle.getString('output.prompt'))
            } else {
                model.setStatus("No directory selected!")
                sleep(2000)
                model.setStatus(bundle.getString('ugconvert.prompt'))
            }


        } else {
            FileChooser fileChooser = new FileChooser()
            fileChooser.setTitle("Select a File to convert...")
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("All NX Files", "*.prt"),
                    new ExtensionFilter("NX Dwg Files", "*_dwg?.prt")
            )
            // Show the open file dialog
            File selectedFile = fileChooser.showOpenDialog(owner)
            if (selectedFile) {
                model.setInputFile(selectedFile.getAbsolutePath())
                model.setStatus("Select an output directory...")
            } else {
                System.out.println("File selection cancelled.");
            }

        }


    }

    @FXML
    protected void outputBtnAction() {

        Window owner = statusLabel.getScene().getWindow() // any injected UI element will work

        // Optional: Set initial directory to user's home folder
        File initialDir = new File(System.getProperty("user.home"))


        DirectoryChooser directoryChooser = new DirectoryChooser()
        directoryChooser.setTitle("Select a Directory for converted files...")

        if (initialDir.exists() && initialDir.isDirectory()) {
            directoryChooser.setInitialDirectory(initialDir)
        }

        File selectedDirectory = directoryChooser.showDialog(owner)
        if (selectedDirectory) {
            model.setOutputDir(selectedDirectory.getAbsolutePath())
            model.setStatus("Select an output directory...")
        } else {
            model.setStatus("No directory selected!")
            sleep(2000)
            model.setStatus("Select a Directory for converted files...")
        }


    }

    @FXML
    protected void okayBtnAction() {
        // logger.info("okay button clicked...")
        setStatus("okay button clicked...")

        logger.info("Convert file = ${model.inputFile}")
        logger.info("Output dir = ${model.outputDir}")
        logger.info("Millimeter units = ${model.mmUnits.toString()}")

        // check for valid files
        if ((new File(model.inputFile).exists()) && (new File(model.outputDir).exists())) {


            setStatus("ug convert file does exist")
            setStatus("output dir does exist")

            // for now don't need to check of map file
            // will overwrite if it exists
            try {
                // set working directory to find drawings
                // model.setWorkingDir(new File(model.ugConvertFile).parentFile)

                service.runUgConvert(model)



            } catch (Exception ex) {
                logger.catching(ex)

                // TEST FOR DIALOG
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle('default.title')
                alert.setHeaderText('dialog.exception.header')
                alert.setContentText('dialog.exception.content')

                // Exception ex = new FileNotFoundException("Could not find file blabla.txt");

                // Create expandable Exception.
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String exceptionText = sw.toString();

                // Label label = new Label("The exception stacktrace was:")

                TextArea textArea = new TextArea(exceptionText);
                textArea.setEditable(false);
                textArea.setWrapText(true);

                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                // expContent.add(label, 0, 0);
                expContent.add(textArea, 0, 1);

                // Set expandable Exception into the dialog pane.
                alert.getDialogPane().setExpandableContent(expContent);

                alert.showAndWait()
                System.exit(1)

            }

            // SOME SUCCESS
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle('default.title')
            alert.setHeaderText('dialog.info.header')
            alert.setContentText('dialog.info.content')

            // Create expandable Exception.
            StringWriter sw = new StringWriter()
            PrintWriter pw = new PrintWriter(sw)

            // String exceptionText = model.masterList.toString()
            String exceptionText = model.masterList.join(System.getProperty("line.separator"))

            Label label = new Label('dialog.info.label')

            TextArea textArea = new TextArea(exceptionText)
            textArea.setEditable(false)
            textArea.setWrapText(true)

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS)
            GridPane.setHgrow(textArea, Priority.ALWAYS)

            GridPane expContent = new GridPane()
            expContent.setMaxWidth(Double.MAX_VALUE)
            expContent.add(label, 0, 0)
            expContent.add(textArea, 0, 1)

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent)

            alert.showAndWait()
            System.exit(0)

        } else {
            setStatus(bundle.getString('default.file.dir.missing'))
        }

    }


    void helpBtnAction() {

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle('default.title')
        alert.setHeaderText('dialog.help.header')
        alert.setContentText('help.label')

        alert.showAndWait()

    }

    // use to set model status and pause 0.5 sec for user to read status line in UI
    // also output same message to system.out and logger
    private void setStatus(String s) {
        System.out.println(s);
        logger.info(s)
        model.setStatus(s);
        try {
            // TimeUnit.MILLISECONDS.sleep(500);
            sleep(500)
        } catch (InterruptedException ex) {
            // Logger.getLogger(ConvertUnitsController.class.getName()).log(Level.SEVERE, null, ex);
            logger.error(ex)
        }
    }


}