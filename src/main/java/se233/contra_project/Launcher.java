package se233.contra_project;

import se233.contra_project.ui.StartScreen;
import se233.contra_project.ui.GameScreen;
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
    private Scene gameScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Contra - Boss Demo");
        primaryStage.setResizable(false);

        // Create StartScreen
        StartScreen startScreen = new StartScreen();
        startScreen.setLauncher(this);
        startScene = new Scene(startScreen, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Create GameScreen
        GameScreen gameScreen = new GameScreen();
        gameScene = new Scene(gameScreen, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Set user data for scene switching
        primaryStage.setUserData(this);

        // Start with StartScreen
        primaryStage.setScene(startScene);
        primaryStage.show();
        startScreen.requestFocus();
    }

    public void switchToGameScreen() {
        primaryStage.setScene(gameScene);
        if (gameScene.getRoot() != null) {
            gameScene.getRoot().requestFocus();
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
