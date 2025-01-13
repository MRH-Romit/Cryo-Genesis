module com.example.mars {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.mars to javafx.fxml;
    exports com.example.mars;
    exports com.example.mars.main;
    opens com.example.mars.main to javafx.fxml;
    exports com.example.mars.keyHandle;
    opens com.example.mars.keyHandle to javafx.fxml;
}