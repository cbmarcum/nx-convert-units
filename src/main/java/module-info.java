module com.copeland.nx.convertunits {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.groovy;

    requires org.kordamp.ikonli.javafx;

    opens com.copeland.nx.convertunits to javafx.fxml;
    exports com.copeland.nx.convertunits;
}