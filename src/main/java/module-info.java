module se233.contra_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.swing;
    requires java.logging;


    opens se233.contra_project to javafx.fxml;
    exports se233.contra_project;
}
