module com.example.slotmachineapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;

    opens com.example.slotmachineapp to javafx.fxml;
    exports com.example.slotmachineapp;
}