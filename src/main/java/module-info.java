module jemb.bistrogurmand {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires com.fasterxml.jackson.databind;

    opens jemb.bistrogurmand.utils to javafx.base, javafx.fxml;
    opens jemb.bistrogurmand.utils.Modals to javafx.base;
    exports jemb.bistrogurmand.application;
}
