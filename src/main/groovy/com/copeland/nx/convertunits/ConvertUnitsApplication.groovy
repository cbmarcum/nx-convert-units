package com.copeland.nx.convertunits

import groovy.transform.CompileStatic
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

@CompileStatic
class ConvertUnitsApplication extends Application {
    @Override
    void start(Stage stage) {
        def fxmlLoader = new FXMLLoader(ConvertUnitsApplication.class.getResource("convert-units-view.fxml"))
        def scene = new Scene(fxmlLoader.load() as Parent, 640, 480)
        stage.setTitle("NX Convert Units")
        stage.setScene(scene)
        stage.show()
    }
}
