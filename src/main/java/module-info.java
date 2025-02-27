module se233.project2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    opens se233.project2 to javafx.fxml;
    exports se233.project2;
    exports se233.project2.controller;
    exports se233.project2.entity;
    exports se233.project2.gameLoop;
    opens se233.project2.controller to javafx.fxml;
}