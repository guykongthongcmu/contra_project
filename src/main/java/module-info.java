open module se233.contra_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.swing;
    requires java.logging;


    exports se233.contra_project to javafx.fxml;
    exports se233.contra_project.actors to org.junit.platform.commons;
    exports se233.contra_project.game to org.junit.platform.commons;
    exports se233.contra_project.ui to org.junit.platform.commons;
}
