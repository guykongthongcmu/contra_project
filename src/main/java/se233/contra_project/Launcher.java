package se233.contra_project;

import se233.contra_project.ui.StartScreen;
import se233.contra_project.ui.Stage1Screen;
import se233.contra_project.ui.Stage2Screen;
import se233.contra_project.ui.Stage3Screen;
import se233.contra_project.ui.BossDemoScreen;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

public class Launcher extends Application {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private Stage primaryStage;
    private Scene startScene;
    private Scene stage1Scene;
    private Scene stage2Scene;
    private Scene stage3Scene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Contra - Boss Demo");
        primaryStage.setResizable(false);

        // Create StartScreen
        StartScreen startScreen = new StartScreen();
        startScreen.setLauncher(this);
        startScene = new Scene(startScreen, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Create Stage screens
        Stage1Screen stage1Screen = new Stage1Screen();
        stage1Scene = new Scene(stage1Screen, WINDOW_WIDTH, WINDOW_HEIGHT);

        Stage2Screen stage2Screen = new Stage2Screen();
        stage2Scene = new Scene(stage2Screen, WINDOW_WIDTH, WINDOW_HEIGHT);

        Stage3Screen stage3Screen = new Stage3Screen();
        stage3Scene = new Scene(stage3Screen, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set user data for scene switching
        primaryStage.setUserData(this);

        // Start with StartScreen
        primaryStage.setScene(startScene);
        primaryStage.show();
        startScreen.requestFocus();
    }

    public void switchToGameScreen() {
        switchToStage(1);
    }

    public void switchToStage(int stageIndex) {
        Scene sceneToShow;
        switch (stageIndex) {
            case 2:
                sceneToShow = stage2Scene;
                break;
            case 3:
                sceneToShow = stage3Scene;
                break;
            case 1:
            default:
                sceneToShow = stage1Scene;
                break;
        }

        primaryStage.setScene(sceneToShow);
        if (sceneToShow.getRoot() != null) {
            sceneToShow.getRoot().requestFocus();
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
