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

        Locale locale = Locale.ENGLISH  // ← or from config / system default
        // Locale locale = new Locale("es")

        ResourceBundle bundle = ResourceBundle.getBundle(
                "com.copeland.nx.convertunits.i18n.messages",
                locale,
                this.class.classLoader                  // ← crucial!
        )

        def fxmlLoader = new FXMLLoader(ConvertUnitsApplication.class.getResource("convert-units-view.fxml"), bundle)
        def scene = new Scene(fxmlLoader.load() as Parent, 640, 480)
        stage.setTitle(bundle.getString('default.title'))
        stage.setScene(scene)
        stage.show()
    }
}
