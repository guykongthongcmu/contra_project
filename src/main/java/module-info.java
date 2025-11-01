module se233.contra_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;


    opens se233.contra_project to javafx.fxml;
    exports se233.contra_project;
}