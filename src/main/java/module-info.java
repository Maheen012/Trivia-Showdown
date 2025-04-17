module org.example.client {
    requires javafx.controls; // JavaFX Controls module
    requires javafx.fxml; // JavaFX FXML module
    requires javafx.base; // JavaFX Base module
    requires javafx.graphics; // JavaFX Graphics module
    requires com.opencsv;
    requires java.desktop;
    exports org.example.client;
    opens org.example.client to javafx.fxml;

}