module com.example.mars {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.mars to javafx.fxml;
    exports com.example.mars;
}