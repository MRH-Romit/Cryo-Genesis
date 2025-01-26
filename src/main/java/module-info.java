module com.example.mars {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    // Export and open the main package
    exports com.example.mars;
    opens com.example.mars to javafx.fxml;

    // Export and open the main.main package
    exports com.example.mars.main;
    opens com.example.mars.main to javafx.fxml;

    // Export and open the Socket package
    exports com.example.mars.Socket;
    opens com.example.mars.Socket to javafx.fxml;

    // Export and open the tiles package
    exports com.example.mars.tiles;
    opens com.example.mars.tiles to javafx.fxml;

    // Export and open Entity package if needed
    exports com.example.mars.Entity;
    opens com.example.mars.Entity to javafx.fxml;

    // Export and open keyHandle package if needed
    exports com.example.mars.keyHandle;
    opens com.example.mars.keyHandle to javafx.fxml;

    exports com.example.mars.puzzle1;
    opens com.example.mars.puzzle1 to javafx.fxml;
}