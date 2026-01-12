module com.copeland.nx.convertunits {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.groovy;

    requires org.kordamp.ikonli.javafx;
    requires org.apache.logging.log4j;
    requires org.apache.commons.io;
    requires org.apache.commons.exec;

    opens com.copeland.nx.convertunits to javafx.fxml;
    exports com.copeland.nx.convertunits;
}